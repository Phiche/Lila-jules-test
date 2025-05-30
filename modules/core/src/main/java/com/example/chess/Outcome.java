package com.example.chess;
import java.util.Optional;
// Basic placeholder
public class Outcome {
    private final Optional<Color> winner;
    public Outcome(Optional<Color> winner) { this.winner = winner; }
    public Optional<Color> getWinner() { return winner; }
}
