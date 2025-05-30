package com.example.game;

// Corresponds to PgnDump.WithFlags in Scala
public class PgnDumpFlags {
    private final boolean clocks;
    private final boolean moves;
    private final boolean tags;
    private final boolean evals;
    private final boolean opening;
    private final boolean rating;
    private final boolean literate;
    private final boolean pgnInJson;
    private final boolean delayMoves;
    private final boolean lastFen;
    private final boolean accuracy;
    private final boolean division;
    private final boolean bookmark;

    // Constructor with all fields, providing defaults similar to Scala's case class defaults
    public PgnDumpFlags(
            boolean clocks, boolean moves, boolean tags, boolean evals, boolean opening,
            boolean rating, boolean literate, boolean pgnInJson, boolean delayMoves,
            boolean lastFen, boolean accuracy, boolean division, boolean bookmark) {
        this.clocks = clocks;
        this.moves = moves;
        this.tags = tags;
        this.evals = evals;
        this.opening = opening;
        this.rating = rating;
        this.literate = literate;
        this.pgnInJson = pgnInJson;
        this.delayMoves = delayMoves;
        this.lastFen = lastFen;
        this.accuracy = accuracy;
        this.division = division;
        this.bookmark = bookmark;
    }

    // Default constructor using typical default values from Scala
    public PgnDumpFlags() {
        this(true, true, true, true, true, true, false, false, false, false, false, false, false);
    }

    // Getters
    public boolean isClocks() { return clocks; }
    public boolean isMoves() { return moves; }
    public boolean isTags() { return tags; }
    public boolean isEvals() { return evals; }
    public boolean isOpening() { return opening; }
    public boolean isRating() { return rating; }
    public boolean isLiterate() { return literate; }
    public boolean isPgnInJson() { return pgnInJson; }
    public boolean isDelayMoves() { return delayMoves; }
    public boolean isLastFen() { return lastFen; }
    public boolean isAccuracy() { return accuracy; }
    public boolean isDivision() { return division; }
    public boolean isBookmark() { return bookmark; }

    public boolean requiresAnalysis() {
        return evals || accuracy;
    }

    // Method to create a new instance with modified delayMoves, mimicking Scala's copy
    public PgnDumpFlags keepDelayIf(boolean condition) {
        if (this.delayMoves && condition) {
            return this; // No change needed if delayMoves is already true and condition is true
        } else if (!this.delayMoves && !condition) {
             // This condition means delayMoves is false, and we want it to remain false (delayMoves && false = false)
             // Or if condition is true, but delayMoves is false, it should still be false.
             // The logic is simply: new delayMoves = current delayMoves AND condition.
             // If they are already equal, no new instance is needed.
            if (this.delayMoves == (this.delayMoves && condition)) { // Check if the outcome is the same
                return this;
            }
        }
        // If we reach here, it means the value of delayMoves will change or it's the same but we didn't hit the exact optimization above.
        return new PgnDumpFlags(
            this.clocks, this.moves, this.tags, this.evals, this.opening, this.rating,
            this.literate, this.pgnInJson, this.delayMoves && condition, // updated value
            this.lastFen, this.accuracy, this.division, this.bookmark
        );
    }
}
