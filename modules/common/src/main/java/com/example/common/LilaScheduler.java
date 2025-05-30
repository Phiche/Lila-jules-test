package com.example.common;

// These imports are not directly used by LilaScheduler but were in the prompt's context.
// They might be used by callers when creating the Duration suppliers.
// import com.example.config.AtMost;
// import com.example.config.Delay;
// import com.example.config.Every;


import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException; // For scheduler rejections
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean; // Though not used in current structure
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LilaScheduler {

    private static final Logger logger = LoggerFactory.getLogger(LilaScheduler.class);

    private LilaScheduler() {}

    /**
     * Schedules a function to be run periodically.
     * The next task is scheduled after the current one completes (or fails/times out).
     * This inherently prevents concurrent execution of the same scheduled task instance.
     *
     * @param name           The name of the scheduled task (for logging).
     * @param everySupplier  Supplier for the interval between task completions and next start.
     * @param timeoutSupplier Supplier for the maximum execution time for the task.
     * @param initialDelaySupplier Supplier for the initial delay before the first execution.
     * @param task           The function to execute, returning a CompletableFuture<Void>.
     * @param executor       Executor for running the task's completion stages.
     * @param scheduler      ScheduledExecutorService for scheduling delays.
     */
    public static void schedule(
            String name,
            Supplier<Duration> everySupplier,
            Supplier<Duration> timeoutSupplier,
            Supplier<Duration> initialDelaySupplier,
            Supplier<CompletableFuture<Void>> task,
            Executor executor, // Executor for whenCompleteAsync's actions
            ScheduledExecutorService scheduler) { // Scheduler for delays

        // The Runnable that performs the work and reschedules itself.
        Runnable scheduledRun = new Runnable() {
            @Override
            public void run() {
                Duration timeout;
                CompletableFuture<Void> taskFuture;

                try {
                    timeout = timeoutSupplier.get();
                    taskFuture = task.get(); // Obtain the future from the supplier
                } catch (Exception e) {
                    logger.error("[LilaScheduler {}] Failed to get task or timeout: {}", name, e.getMessage(), e);
                    // Attempt to reschedule even if getting the task failed, to maintain periodic execution.
                    try {
                        Duration every = everySupplier.get();
                        scheduler.schedule(this, every.toMillis(), TimeUnit.MILLISECONDS);
                    } catch (Exception rescheduleEx) {
                        logger.error("[LilaScheduler {}] Failed to reschedule after task supplier error: {}", name, rescheduleEx.getMessage(), rescheduleEx);
                    }
                    return; // Do not proceed with this execution if task/timeout supplier failed
                }

                taskFuture.orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                    .whenCompleteAsync((result, throwable) -> {
                        if (throwable != null) {
                            logger.warn("[LilaScheduler {}] Task execution failed or timed out: {}", name, throwable.getMessage(), throwable);
                        }
                        // Always reschedule for the next run
                        try {
                            Duration every = everySupplier.get();
                            scheduler.schedule(this, every.toMillis(), TimeUnit.MILLISECONDS);
                        } catch (RejectedExecutionException ree) {
                            logger.warn("[LilaScheduler {}] Could not reschedule task, scheduler likely shutting down: {}", name, ree.getMessage());
                        } catch (Exception e) {
                            logger.error("[LilaScheduler {}] Failed to reschedule task: {}", name, e.getMessage(), e);
                        }
                    }, executor);
            }
        };

        try {
            Duration initialDelay = initialDelaySupplier.get();
            scheduler.schedule(scheduledRun, initialDelay.toMillis(), TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ree) {
            logger.warn("[LilaScheduler {}] Could not schedule initial task, scheduler likely shutting down: {}", name, ree.getMessage());
        } catch (Exception e) {
            logger.error("[LilaScheduler {}] Failed to schedule initial task: {}", name, e.getMessage(), e);
        }
    }

    /**
     * Schedules a function with variable delay based on the previous result.
     *
     * @param name           The name of the scheduled task.
     * @param delayFunction  Function to determine the next delay, taking Optional of previous result.
     * @param timeoutSupplier Supplier for the maximum execution time.
     * @param initialDelaySupplier Supplier for the initial delay.
     * @param task           The function to execute, returning CompletableFuture<A>.
     * @param executor       Executor for running the task's completion stages.
     * @param scheduler      ScheduledExecutorService for scheduling delays.
     */
    public static <A> void scheduleWithVariableDelay(
            String name,
            Function<Optional<A>, Duration> delayFunction,
            Supplier<Duration> timeoutSupplier,
            Supplier<Duration> initialDelaySupplier,
            Supplier<CompletableFuture<A>> task,
            Executor executor,
            ScheduledExecutorService scheduler) {

        Runnable scheduledRun = new Runnable() {
            @Override
            public void run() {
                Duration timeout;
                CompletableFuture<A> taskFuture;

                try {
                    timeout = timeoutSupplier.get();
                    taskFuture = task.get();
                } catch (Exception e) {
                    logger.error("[LilaScheduler {}] Failed to get task or timeout for variable delay task: {}", name, e.getMessage(), e);
                    // Attempt to reschedule with a default delay or based on empty result
                    try {
                        Duration nextDelay = delayFunction.apply(Optional.empty());
                        scheduler.schedule(this, nextDelay.toMillis(), TimeUnit.MILLISECONDS);
                    } catch (Exception rescheduleEx) {
                         logger.error("[LilaScheduler {}] Failed to reschedule variable delay task after supplier error: {}", name, rescheduleEx.getMessage(), rescheduleEx);
                    }
                    return;
                }

                taskFuture.orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                    .whenCompleteAsync((result, throwable) -> {
                        Optional<A> previousResult = Optional.empty();
                        if (throwable != null) {
                            logger.warn("[LilaScheduler {}] Variable delay task execution failed or timed out: {}", name, throwable.getMessage(), throwable);
                            // previousResult remains empty
                        } else {
                            previousResult = Optional.ofNullable(result);
                        }

                        try {
                            Duration nextDelay = delayFunction.apply(previousResult);
                            scheduler.schedule(this, nextDelay.toMillis(), TimeUnit.MILLISECONDS);
                        } catch (RejectedExecutionException ree) {
                            logger.warn("[LilaScheduler {}] Could not reschedule variable delay task, scheduler likely shutting down: {}", name, ree.getMessage());
                        } catch (Exception e) {
                            logger.error("[LilaScheduler {}] Failed to reschedule variable delay task: {}", name, e.getMessage(), e);
                        }
                    }, executor);
            }
        };

        try {
            Duration initialDelay = initialDelaySupplier.get();
            scheduler.schedule(scheduledRun, initialDelay.toMillis(), TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ree) {
             logger.warn("[LilaScheduler {}] Could not schedule initial variable delay task, scheduler likely shutting down: {}", name, ree.getMessage());
        } catch (Exception e) {
             logger.error("[LilaScheduler {}] Failed to schedule initial variable delay task: {}", name, e.getMessage(), e);
        }
    }
}
