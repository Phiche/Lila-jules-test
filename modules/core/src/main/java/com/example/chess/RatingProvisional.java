package com.example.chess;

public enum RatingProvisional {
    YES, NO;
    public boolean isTrue() { return this == YES; } // Scala .value was likely a boolean conversion
}
