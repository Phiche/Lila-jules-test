package com.example.chess;
public class IntRating {
    private final int value;
    public IntRating(int value) { this.value = value; }
    public int getValue() { return value; }
    public IntRating map(java.util.function.Function<Integer, Integer> f) { return new IntRating(f.apply(value)); }
    public static IntRating plus(IntRating a, IntRating b) { return new IntRating(a.value + b.value); }
     @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; IntRating intRating = (IntRating) o; return value == intRating.value; }
    @Override public int hashCode() { return Integer.hashCode(value); }
}
