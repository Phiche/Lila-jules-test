package com.example.lilaism;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BooleanUtils {

    public static <B> CompletableFuture<Optional<B>> soFu(boolean self, Supplier<CompletableFuture<B>> f) {
        if (self) {
            return f.get().thenApply(Optional::of);
        } else {
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }
}
