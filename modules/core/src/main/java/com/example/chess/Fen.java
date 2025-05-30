package com.example.chess;

import java.util.Optional;

// Placeholder for FEN (Forsyth-Edwards Notation) related classes
public class Fen {
    private final String value;

    public Fen(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // Nested static class for Fen.Full
    public static class Full extends Fen {
        public Full(String value) {
            super(value);
        }
    }
    
    // Optional: A static factory method if Fen.Full is constructed from a String
    public static Optional<Full> full(String fenString) {
        // Add validation if necessary
        if (fenString != null && !fenString.isEmpty()) {
            // A more robust FEN validation would go here
            return Optional.of(new Full(fenString));
        }
        return Optional.empty();
    }
}
