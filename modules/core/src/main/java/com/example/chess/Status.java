package com.example.chess;
// Basic placeholder enum
public enum Status {
    CREATED, STARTED, ABORTED, MATE, RESIGN, STALEMATE, TIMEOUT, DRAW, OUTOFTIME, CHEAT, NOSTART, UNKNOWNFINISH, VARIANTEND;
    public boolean isGreaterThanOrEqual(Status other) { return this.ordinal() >= other.ordinal(); } // Approximation
    public static final Status Started = STARTED; // For direct reference
    public static final Status Aborted = ABORTED;
    public static final Status Mate = MATE;
    public static final Status Outoftime = OUTOFTIME;
}
