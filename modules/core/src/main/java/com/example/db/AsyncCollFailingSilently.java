package com.example.db;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

// T will be the type representing the MongoDB collection in the Java driver
// A will be the result type of the operation
@FunctionalInterface
public interface AsyncCollFailingSilently<T, A> {

    /**
     * Applies an operation to a MongoDB collection.
     * The implementation should handle potential failures silently,
     * possibly returning a default/zero value for type A.
     *
     * @param collection The MongoDB collection object.
     * @return A CompletableFuture holding the result of the operation or a default value.
     */
    CompletableFuture<A> apply(T collection);

    // Consider adding a default method here if a common way of handling "Zero" emerges,
    // or it might be handled by the caller/concrete implementation.
    // For example:
    // default CompletableFuture<A> applyOrDefault(T collection, Supplier<A> defaultValueSupplier) {
    //     return apply(collection).exceptionally(ex -> {
    //         // Log the exception (optional)
    //         // System.err.println("Operation failed silently: " + ex.getMessage());
    //         return defaultValueSupplier.get();
    //     });
    // }
}
