package com.example.common;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A simple container to represent the outcome of an operation that might succeed or fail,
 * similar to Scala's Try.
 */
public class TryValue<A> {
    private final A successValue;
    private final Throwable failureValue;

    private TryValue(A success, Throwable failure) {
        this.successValue = success;
        this.failureValue = failure;
    }

    public static <A> TryValue<A> success(A value) {
        return new TryValue<>(value, null);
    }

    public static <A> TryValue<A> failure(Throwable t) {
        if (t == null) { // A failure must have a non-null throwable
            throw new NullPointerException("Throwable cannot be null for a failure TryValue");
        }
        return new TryValue<>(null, t);
    }

    public boolean isSuccess() {
        return failureValue == null;
    }

    public boolean isFailure() {
        return failureValue != null;
    }

    public Optional<A> getSuccess() {
        return Optional.ofNullable(successValue);
    }

    public Optional<Throwable> getFailure() {
        return Optional.ofNullable(failureValue);
    }

    public A get() throws Throwable {
        if (isSuccess()) return successValue;
        throw failureValue;
    }

    public A getOrElse(Supplier<A> defaultSupplier) {
        return isSuccess() ? successValue : defaultSupplier.get();
    }

    @SuppressWarnings("unchecked") // For casting failureValue to TryValue<B> type
    public <B> TryValue<B> map(Function<? super A, ? extends B> f) { // Corrected generic bounds
        if (isSuccess()) {
            try {
                return TryValue.success(f.apply(successValue));
            } catch (Throwable t) {
                return TryValue.failure(t);
            }
        }
        return (TryValue<B>) this; // It's a failure, so it's already TryValue<Nothing> effectively
    }

    @SuppressWarnings("unchecked") // For casting failureValue to TryValue<B> type
    public <B> TryValue<B> flatMap(Function<? super A, ? extends TryValue<B>> f) { // Corrected generic bounds
        if (isSuccess()) {
            try {
                return f.apply(successValue);
            } catch (Throwable t) {
                return TryValue.failure(t);
            }
        }
        return (TryValue<B>) this; // It's a failure
    }
}
