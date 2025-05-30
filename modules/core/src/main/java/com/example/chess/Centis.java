package com.example.chess;
public class Centis {
    private final int value;
    public Centis(int value) { this.value = value; }
    public int getValue() { return value; }
    public static Centis fromSeconds(int seconds) { return new Centis(seconds * 100); }
     @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; Centis centis = (Centis) o; return value == centis.value; }
    @Override public int hashCode() { return Integer.hashCode(value); }
}
