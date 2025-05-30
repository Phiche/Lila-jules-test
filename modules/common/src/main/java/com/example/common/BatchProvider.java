package com.example.common;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList; // Good for queue-like operations (removeFirst)
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger; // Using SLF4J for logging, common in Java
import org.slf4j.LoggerFactory;


public class BatchProvider<A> implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(BatchProvider.class);

    private final String name;
    private final Duration generateBatchTimeout; // Timeout for the generateBatch operation itself
    private final Supplier<CompletableFuture<List<A>>> generateBatchAsync;

    private final List<A> reserve = new LinkedList<>(); // Guarded by access through singleThreadExecutor
    private final ExecutorService singleThreadExecutor;

    public BatchProvider(String name, Duration generateBatchTimeout, Supplier<CompletableFuture<List<A>>> generateBatchAsync) {
        this.name = name;
        this.generateBatchTimeout = generateBatchTimeout;
        this.generateBatchAsync = generateBatchAsync;
        // Using a named thread factory for better debugging/monitoring if needed
        this.singleThreadExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "batch-provider-" + name);
            t.setDaemon(true); // Allow JVM to exit if only daemon threads are running
            return t;
        });
    }

    public CompletableFuture<A> one() {
        CompletableFuture<A> futureResult = new CompletableFuture<>();

        try {
            singleThreadExecutor.submit(() -> {
                if (singleThreadExecutor.isShutdown()) { // Check if executor is shutting down
                    futureResult.completeExceptionally(
                        new IllegalStateException("[" + name + "] BatchProvider is shutting down, cannot process new requests.")
                    );
                    return;
                }

                if (!reserve.isEmpty()) {
                    futureResult.complete(reserve.remove(0));
                } else {
                    CompletableFuture<List<A>> batchFuture = generateBatchAsync.get();

                    batchFuture.orTimeout(generateBatchTimeout.toMillis(), TimeUnit.MILLISECONDS)
                        .whenComplete((batch, throwable) -> {
                            if (throwable != null) {
                                logger.error("[{}] Error generating batch", name, throwable);
                                futureResult.completeExceptionally(
                                    new RuntimeException("[" + name + "] Failed to generate batch: " + throwable.getMessage(), throwable)
                                );
                            } else if (batch == null || batch.isEmpty()) {
                                String errorMessage = "[" + name + "] Couldn't generate batch (returned empty or null)";
                                logger.error(errorMessage);
                                futureResult.completeExceptionally(new IllegalStateException(errorMessage));
                            } else {
                                reserve.addAll(batch);
                                // It's possible the executor was shut down between the submit and this point,
                                // or another thread completed this future due to race conditions if not strictly single-threaded logic.
                                // However, with singleThreadExecutor, this block should be safe.
                                if (!reserve.isEmpty()) {
                                    if (!futureResult.isDone()) { // Complete only if not already completed by an error/timeout
                                        futureResult.complete(reserve.remove(0));
                                    }
                                } else {
                                    // Should not happen if batch was not empty, but as a safeguard
                                    String errorMessage = "[" + name + "] Batch generated but reserve became empty unexpectedly";
                                    logger.error(errorMessage);
                                    if (!futureResult.isDone()) {
                                        futureResult.completeExceptionally(new IllegalStateException(errorMessage));
                                    }
                                }
                            }
                        });
                }
            });
        } catch (RejectedExecutionException e) {
            // Executor might be shutting down
            logger.warn("[{}] Task rejected, BatchProvider is shutting down.", name, e);
            if (!futureResult.isDone()) {
                futureResult.completeExceptionally(
                    new IllegalStateException("[" + name + "] BatchProvider is not accepting new tasks.", e)
                );
            }
        }
        return futureResult;
    }

    /**
     * Shuts down the internal executor. Call this when the BatchProvider is no longer needed.
     * Implements AutoCloseable for use in try-with-resources.
     */
    @Override
    public void close() {
        shutdownExecutor();
    }

    public void shutdownExecutor() {
        if (singleThreadExecutor != null && !singleThreadExecutor.isShutdown()) {
            logger.info("[{}] Shutting down BatchProvider executor.", name);
            singleThreadExecutor.shutdown();
            try {
                // Wait a bit longer than the batch timeout to allow ongoing operations to complete
                if (!singleThreadExecutor.awaitTermination(generateBatchTimeout.toMillis() + 2000, TimeUnit.MILLISECONDS)) {
                    logger.warn("[{}] BatchProvider executor did not terminate gracefully after shutdown, forcing now.", name);
                    singleThreadExecutor.shutdownNow();
                    if (!singleThreadExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                        logger.error("[{}] BatchProvider executor did not terminate even after forced shutdown.", name);
                    }
                }
            } catch (InterruptedException ie) {
                logger.warn("[{}] Interrupted while waiting for BatchProvider executor to terminate.", name, ie);
                singleThreadExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
