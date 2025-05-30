package com.example.chess;
public class ClockConfig {
    private final int limit;
    private final int increment;
    public ClockConfig(int limit, int increment) { this.limit = limit; this.increment = increment; }
    public int getLimit() { return limit; }
    public int getIncrement() { return increment; }
    public boolean berserkable() { return false; } // Placeholder, assuming false by default
}
