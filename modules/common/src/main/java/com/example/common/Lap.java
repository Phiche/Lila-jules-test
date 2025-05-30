package com.example.common;

import com.example.common.log.LilaLogger; // Placeholder
import com.example.common.mon.LilaMon; // Placeholder
import com.example.common.mon.TimerPath; // Placeholder
import com.example.common.mon.KamonTimerPlaceholder; // For mon methods
import java.time.Duration;
import java.util.function.Function;

public class Lap<A> {
    private final A result;
    private final long nanos;

    public Lap(A result, long nanos) {
        this.result = result;
        this.nanos = nanos;
    }

    public A getResult() { return result; }
    public long getNanos() { return nanos; }

    public int getMillis() { return (int) (nanos / 1_000_000L); }
    public int getMicros() { return (int) (nanos / 1_000L); }
    public int getSeconds() { return getMillis() / 1000; }

    public Lap<A> logIfSlow(int thresholdMillis, LilaLogger logger, Function<A, String> msgFunction) {
        if (getMillis() >= thresholdMillis) {
            log(logger, msgFunction);
        }
        return this;
    }

    public Lap<A> log(LilaLogger logger, Function<A, String> msgFunction) {
        if (logger != null) { // Basic null check for placeholder
            logger.info("<" + getMillis() + "ms> " + msgFunction.apply(result));
        } else {
            System.out.println("Logger is null. Lap info: <" + getMillis() + "ms> " + msgFunction.apply(result));
        }
        return this;
    }

    public Lap<A> mon(TimerPath path) {
        // path.timer(LilaMon.INSTANCE).record(nanos); // Original simplified approach
        // Correct usage of KamonTimerPlaceholder would be to get a timer and record on it.
        // If LilaMon.INSTANCE.timer(path) returns a KamonTimerPlaceholder that is not pre-started:
        KamonTimerPlaceholder timer = LilaMon.INSTANCE.timer(path); // Gets a new timer instance
        timer.record(nanos); // Records the externally measured duration
        // System.out.println("[MONITOR] Recording lap to path " + path + ": " + nanos + "ns"); // Covered by record
        return this;
    }

    public Lap<A> monValue(Function<A, TimerPath> pathFunction) {
        TimerPath path = pathFunction.apply(result);
        // path.timer(LilaMon.INSTANCE).record(nanos);
        KamonTimerPlaceholder timer = LilaMon.INSTANCE.timer(path);
        timer.record(nanos);
        // System.out.println("[MONITOR] Recording lap to value-derived path " + pathFunction.apply(result) + ": " + nanos + "ns");
        return this;
    }

    public A pp() {
        System.out.println("chrono " + showDuration());
        return result;
    }

    public A pp(String msg) {
        System.out.println("chrono " + msg + " - " + showDuration());
        return result;
    }

    public A ppIfGt(String msg, Duration duration) {
        if (nanos > duration.toNanos()) {
            return pp(msg);
        }
        return result;
    }

    public String showDuration() {
        if (getMillis() >= 1) {
            return getMillis() + " ms";
        }
        if (getMicros() >= 1) { // Added check for micros > 0 before showing
            return getMicros() + " µs"; // Changed to µs for micros
        }
        return nanos + " ns"; // Show nanos if less than 1 micro
    }
}
