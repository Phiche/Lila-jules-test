package com.example.common.misc.lpv;

import com.example.common.PgnStr; // Assuming PgnStr from com.example.common

// Placeholder for lila.core.misc.lpv.LpvEmbed
// This was a sealed trait in Scala with case objects/classes.
// In Java, we can use an interface and implementing classes, or an enum if states are fixed.
public interface LpvEmbed {
    // Using specific classes for different types of embeds for type safety
    class PublicPgn implements LpvEmbed {
        private final PgnStr pgn;
        public PublicPgn(PgnStr pgn) { this.pgn = pgn; }
        public PgnStr getPgn() { return pgn; }
    }

    class PrivateStudy implements LpvEmbed {
        // No specific data, just its type matters for rendering
        public PrivateStudy() {}
    }

    // Add other types if LpvEmbed had more subtypes in original lila.core.misc.lpv
}
