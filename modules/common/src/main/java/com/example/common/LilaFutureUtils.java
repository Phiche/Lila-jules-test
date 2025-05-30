package com.example.common;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class LilaFutureUtils {
    private LilaFutureUtils() {}

    /**
     * Ensures that a CompletableFuture runs for at least a minimum duration.
     * If the original future completes faster than the minimum duration,
     * the returned future will complete after the minimum duration has passed.
     * The result of the original future is preserved.
     *
     * @param minDuration The minimum duration for the operation.
     * @param futureSupplier A supplier for the CompletableFuture to execute.
     * @param scheduler A ScheduledExecutorService to handle delays.
     * @return A CompletableFuture that completes with the result of the original future,
     *         after at least minDuration has passed from the call to this method.
     */
    public static <T> CompletableFuture<T> makeItLast(
            Duration minDuration,
            Supplier<CompletableFuture<T>> futureSupplier,
            ScheduledExecutorService scheduler) {
        
        long startTimeNanos = System.nanoTime();
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
        }, scheduler); // Use the scheduler for composing as well, or a common pool
    }
}
