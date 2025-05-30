package com.example.common;

/**
 * Represents an isomorphism between types A and B, allowing conversion in both directions.
 * @param <A> The first type.
 * @param <B> The second type.
 */
public interface Iso<A, B> {
    B to(A a);
    A from(B b);
}
