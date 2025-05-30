package com.example.chess;
// Basic placeholder enum
public enum Variant {
    STANDARD, CHESS960, CRAZYHOUSE, ANTICHESS, ATOMIC, HORDE, KINGOFTHEHILL, RACINGKINGS, THREECHECK, FROM_POSITION;
    public boolean isRacingKings() { return this == RACINGKINGS; }
    public boolean isFromPosition() { return this == FROM_POSITION; }
    public static final Variant Standard = STANDARD; // For direct reference
}
