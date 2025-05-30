package com.example.lilaism;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function; // For Either

// Assuming a simplified Either for now, or it would be a dependency
// For demonstration, let's assume Either<L, R> has isLeft(), getLeft(), isRight(), getRight()

public class EitherUtils {

    // This is a placeholder. A proper Either<L,R> type would be needed.
    // For now, let's assume we'll adapt this if we encounter a specific Either implementation.
    // public static <L, R> CompletableFuture<R> toFuture(Either<L, R> self) {
    // if (self.isRight()) {
    // return CompletableFuture.completedFuture(self.getRight());
    // } else {
    // L left = self.getLeft();
    // if (left instanceof Throwable) {
    // return FutureUtils.fufail((Throwable) left);
    // } else {
    // return FutureUtils.fufail(left.toString());
    // }
    // }
    // }
}
