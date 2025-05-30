package com.example.common;

import com.example.common.log.LilaLogger; // Assuming placeholder exists

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor; // Added for executor parameters
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class LilaFutureUtils {
    private LilaFutureUtils() {}

    /**
     * Ensures that a CompletableFuture runs for at least a minimum duration.
     * (Existing method - provided for context if it needs to be in the same file)
     */
    public static <T> CompletableFuture<T> makeItLast(
            Duration minDuration,
            Supplier<CompletableFuture<T>> futureSupplier,
            ScheduledExecutorService scheduler,
            Executor executor) {

        if (minDuration == null || minDuration.isZero() || minDuration.isNegative()) {
            return futureSupplier.get();
        }

        long startTimeNanos = System.nanoTime(); // Using System.nanoTime() directly
        CompletableFuture<T> originalFuture = futureSupplier.get();

        return originalFuture.thenComposeAsync(result -> {
            long elapsedNanos = System.nanoTime() - startTimeNanos;
            long remainingNanos = minDuration.toNanos() - elapsedNanos;

            if (remainingNanos > 0) {
                CompletableFuture<T> delayedFuture = new CompletableFuture<>();
                scheduler.schedule(() -> delayedFuture.complete(result), remainingNanos, TimeUnit.NANOSECONDS);
                return delayedFuture;
            } else {
                return CompletableFuture.completedFuture(result);
            }
        }, executor); // Use the provided executor for composition
    }

    /**
     * Delays the execution of a CompletableFuture-producing supplier.
     */
    public static <A> CompletableFuture<A> delay(
            Duration duration,
            Supplier<CompletableFuture<A>> runSupplier,
            ScheduledExecutorService scheduler,
            Executor executor) {

        if (duration == null || duration.isZero() || duration.isNegative()) {
            // Execute immediately on the provided executor and flatten
            return CompletableFuture.supplyAsync(runSupplier, executor).thenCompose(cf -> cf);
        }

        CompletableFuture<A> resultFuture = new CompletableFuture<>();
        scheduler.schedule(() -> {
            try {
                CompletableFuture.supplyAsync(runSupplier, executor)
                    .thenCompose(cf -> cf)
                    .whenComplete((res, err) -> {
                        if (err != null) {
                            resultFuture.completeExceptionally(err);
                        } else {
                            resultFuture.complete(res);
                        }
                    });
            } catch (Exception e) {
                resultFuture.completeExceptionally(e);
            }
        }, duration.toMillis(), TimeUnit.MILLISECONDS);
        return resultFuture;
    }

    /**
     * Returns a CompletableFuture that completes successfully with null after a given duration.
     */
    public static CompletableFuture<Void> sleep(
            Duration duration,
            ScheduledExecutorService scheduler) {

        if (duration == null || duration.isZero() || duration.isNegative()) {
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<Void> future = new CompletableFuture<>();
        scheduler.schedule(() -> future.complete(null), duration.toMillis(), TimeUnit.MILLISECONDS);
        return future;
    }

    /**
     * Retries a CompletableFuture-producing operation a specified number of times with a delay.
     */
    public static <T> CompletableFuture<T> retry(
            Supplier<CompletableFuture<T>> operation,
            Duration delayDuration,
            int retries,
            Optional<LilaLogger> loggerOpt,
            ScheduledExecutorService scheduler,
            Executor executor) {

        CompletableFuture<T> resultPromise = new CompletableFuture<>();

        class RetryAttempt { // Changed to inner class for state, not a direct Supplier for CompletableFuture
            private int currentAttempt = 0;

            void attempt() {
                currentAttempt++;
                operation.get().whenCompleteAsync((result, error) -> {
                    if (error != null) {
                        if (currentAttempt <= retries) {
                            final int attemptNum = currentAttempt;
                            loggerOpt.ifPresent(log -> log.info(
                                "[" + attemptNum + "/" + retries + "] retrying operation - error: " + error.getMessage()
                            ));
                            try {
                                scheduler.schedule(this::attempt, delayDuration.toMillis(), TimeUnit.MILLISECONDS);
                            } catch (Exception e_sched) {
                                resultPromise.completeExceptionally(e_sched);
                            }
                        } else {
                            loggerOpt.ifPresent(log -> log.warn(
                                "Operation failed after " + retries + " retries: " + error.getMessage(), error
                            ));
                            resultPromise.completeExceptionally(error);
                        }
                    } else {
                        resultPromise.complete(result);
                    }
                }, executor);
            }
        }

        new RetryAttempt().attempt(); // Initial call to start the process
        return resultPromise;
    }
}
