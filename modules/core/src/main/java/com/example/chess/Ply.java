package com.example.chess;

import java.util.Comparator;
import java.util.Objects;

public class Ply implements Comparable<Ply> { // Added Comparable
    private final int value;

    public static final Ply INITIAL = new Ply(0); // Renamed from initial() for clarity as a constant
    public static final Comparator<Ply> PLY_COMPARATOR = Comparator.comparingInt(Ply::getValue);


    public Ply(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Color getTurn() {
        return (value % 2 == 0) ? Color.WHITE : Color.BLACK; // Simplified, assumes Ply 0 is White's first move
    }

    public Ply next() {
        return new Ply(value + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ply ply = (Ply) o;
        return value == ply.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compareTo(Ply other) { // Implemented compareTo
        return Integer.compare(this.value, other.value);
    }

    @Override
    public String toString() {
        return "Ply(" + value + ")";
    }
}
