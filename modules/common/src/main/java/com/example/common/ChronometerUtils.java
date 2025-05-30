package com.example.common;

import com.example.common.mon.LilaMon; // Placeholder
import com.example.common.mon.TimerPath; // Placeholder
import com.example.common.mon.KamonTimerPlaceholder; // Placeholder
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ChronometerUtils {

    private ChronometerUtils() {}

    public static long nowNanosRel() {
        return System.nanoTime(); // System.nanoTime() is for relative time
    }

    public static <A> Lap<A> timeSync(Supplier<A> supplier) {
        long startNanos = nowNanosRel();
        A res = supplier.get();
        return new Lap<>(res, nowNanosRel() - startNanos);
    }

    public static <A> A timeSyncEffect(Supplier<A> supplier, Consumer<Lap<A>> effect) {
        Lap<A> lap = timeSync(supplier);
        effect.accept(lap);
        return lap.getResult();
    }

    public static <A> CompletableFuture<Lap<A>> timeAsync(Supplier<CompletableFuture<A>> futureSupplier) {
        long startNanos = nowNanosRel();
        return futureSupplier.get().thenApply(res -> new Lap<>(res, nowNanosRel() - startNanos));
    }

    // syncMon and other Kamon-related methods are stubbed or simplified for now
    public static <A> A timeSyncMon(TimerPath path, Supplier<A> supplier) {
        // KamonTimerPlaceholder timer = path.timer(LilaMon.INSTANCE).start(); // This would require start() to return a started timer
        // For the current KamonTimerPlaceholder, its constructor starts timing.
        KamonTimerPlaceholder timer = LilaMon.INSTANCE.timer(path); // LilaMon.timer(path) returns a new timer
        try {
            return supplier.get();
        } finally {
            timer.stop(); // stop() calculates duration from construction and records
        }
    }

    public static Supplier<Lap<Void>> start() {
        long startNanos = nowNanosRel();
        return () -> new Lap<>(null, nowNanosRel() - startNanos);
    }
}
