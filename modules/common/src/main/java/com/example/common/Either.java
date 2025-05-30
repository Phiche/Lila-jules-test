package com.example.common;

import java.util.NoSuchElementException;
import java.util.Objects; // For Objects.requireNonNull
import java.util.Optional;
import java.util.function.Function;

public final class Either<L, R> {
    private final L left;
    private final R right;
    private final boolean isLeft;

    private Either(L left, R right, boolean isLeft) {
        this.left = left;
        this.right = right;
        this.isLeft = isLeft;
    }

    public static <L, R> Either<L, R> left(L value) {
        Objects.requireNonNull(value, "Left value cannot be null");
        return new Either<>(value, null, true);
    }

    public static <L, R> Either<L, R> right(R value) {
        Objects.requireNonNull(value, "Right value cannot be null");
        return new Either<>(null, value, false);
    }

    public boolean isLeft() {
        return isLeft;
    }

    public boolean isRight() {
        return !isLeft;
    }

    public L getLeft() {
        if (!isLeft) throw new NoSuchElementException("Either.getLeft() on a Right value");
        return left;
    }

    public R getRight() {
        if (isLeft) throw new NoSuchElementException("Either.getRight() on a Left value");
        return right;
    }
    
    public Optional<L> getLeftOption() {
        return isLeft ? Optional.of(left) : Optional.empty();
    }

    public Optional<R> getRightOption() {
        return !isLeft ? Optional.of(right) : Optional.empty();
    }
    
    // Added toOption from Scala's Either for the substitute case
    public Optional<R> toOption() {
        return getRightOption();
    }

    public <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper) {
        Objects.requireNonNull(leftMapper, "Left mapper function cannot be null");
        Objects.requireNonNull(rightMapper, "Right mapper function cannot be null");
        return isLeft ? leftMapper.apply(left) : rightMapper.apply(right);
    }
    
    // Add other useful methods like map, flatMap if needed later
    // Example map (maps the right side, or keeps left as is)
    @SuppressWarnings("unchecked")
    public <R2> Either<L, R2> map(Function<? super R, ? extends R2> f) {
        Objects.requireNonNull(f, "Mapping function cannot be null");
        if (isRight()) {
            return Either.right(f.apply(right));
        }
        return (Either<L, R2>) this;
    }

    // Example flatMap (maps the right side, or keeps left as is)
    @SuppressWarnings("unchecked")
    public <R2> Either<L, R2> flatMap(Function<? super R, ? extends Either<L, R2>> f) {
        Objects.requireNonNull(f, "FlatMapping function cannot be null");
        if (isRight()) {
            return f.apply(right);
        }
        return (Either<L, R2>) this;
    }
}
