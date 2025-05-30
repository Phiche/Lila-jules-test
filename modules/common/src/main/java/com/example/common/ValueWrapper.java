package com.example.common;

import java.util.Objects;

/**
 * A simple generic wrapper class for holding a single value.
 * Useful for creating strongly-typed "newtypes" or value classes.
 * @param <T> The type of the value being wrapped.
 */
public abstract class ValueWrapper<T> {
    protected final T value;

    protected ValueWrapper(T value) {
        // In Scala, the value class cannot be null.
        // For Java, we might allow null if T is not a primitive,
        // but often these wrappers imply non-nullability.
        this.value = Objects.requireNonNull(value, "ValueWrapper cannot wrap a null value.");
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueWrapper<?> that = (ValueWrapper<?>) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode(); // Using value's hashCode directly
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
