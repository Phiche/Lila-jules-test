package com.example.chess;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
// Basic placeholder
public class ChessGame {
    public Board getPosition() { return new Board(); } // Placeholder
    public Ply getPly() { return new Ply(0); } // Placeholder
    public Optional<Clock> getClock() { return Optional.empty(); } // Placeholder
    public List<SanStr> getSans() { return Collections.emptyList(); } // Placeholder
    public Ply getStartedAtPly() { return new Ply(0); } // Placeholder
    public Color getPlayer() { return Color.WHITE; } // Placeholder, current player to move
    public History getHistory() { return new History(); } // Placeholder
    public Variant getVariant() { return Variant.STANDARD; } // Placeholder
}
