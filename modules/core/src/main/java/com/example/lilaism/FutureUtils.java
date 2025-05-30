package com.example.lilaism;

import java.util.concurrent.CompletableFuture;

public class FutureUtils {

    public static <A> CompletableFuture<A> fuccess(A a) {
        return CompletableFuture.completedFuture(a);
    }

    public static <X> CompletableFuture<X> fufail(Throwable t) {
        CompletableFuture<X> future = new CompletableFuture<>();
        future.completeExceptionally(t);
        return future;
    }

    public static <X> CompletableFuture<X> fufail(String s) {
        return fufail(new LilaInvalid(s)); // Assuming LilaInvalid is available
    }

    public static CompletableFuture<Void> funit() {
        return CompletableFuture.completedFuture(null);
    }

    public static CompletableFuture<Boolean> fuTrue() {
        return CompletableFuture.completedFuture(true);
    }

    public static CompletableFuture<Boolean> fuFalse() {
        return CompletableFuture.completedFuture(false);
    }
}
