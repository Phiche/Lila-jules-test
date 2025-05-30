package com.example.chess;
// Basic placeholder
public class Mode {
    private final boolean rated;
    public Mode(boolean rated) { this.rated = rated; }
    public boolean isRated() { return rated; }
    public boolean isCasual() { return !rated; }
    public static Mode defaultMode() { return new Mode(false); } // Placeholder
}
