package com.example.chess;

import java.util.Optional; 
import java.util.Collections; 
import java.util.List; 
import java.util.Objects; // For Objects.requireNonNull


// Basic placeholder for chess.Opening
public class Opening {
    private final String code; // ECO code like "A00"
    private final String name;
    private final OpeningFamily family; // Reference to its family
    private final Optional<String> variation; // e.g., "King's Gambit Accepted" might have variation "Accepted"
    private final int nbMoves; // Number of moves in this specific opening line
    private final List<SanStr> exampleMoves; // Example moves for this opening


    // Constructor for a more complete Opening object
    public Opening(String code, String name, OpeningFamily family, Optional<String> variation, int nbMoves, List<SanStr> exampleMoves) {
        this.code = Objects.requireNonNull(code, "Opening code cannot be null");
        this.name = Objects.requireNonNull(name, "Opening name cannot be null");
        this.family = Objects.requireNonNull(family, "Opening family cannot be null");
        this.variation = Objects.requireNonNull(variation, "Opening variation Optional cannot be null");
        this.nbMoves = nbMoves;
        this.exampleMoves = exampleMoves == null ? Collections.emptyList() : Collections.unmodifiableList(exampleMoves);
    }
    
    // Simpler constructor if less detail is initially available from a flat structure
    public Opening(String code, String name, String familyKey, String familyName, String variationName, int plyCount) {
        this.code = Objects.requireNonNull(code, "Opening code cannot be null");
        this.name = Objects.requireNonNull(name, "Opening name cannot be null");
        this.family = new OpeningFamily(
            Objects.requireNonNull(familyKey, "Family key cannot be null"), 
            Objects.requireNonNull(familyName, "Family name cannot be null")
        );
        this.variation = Optional.ofNullable(variationName).filter(s -> !s.isEmpty());
        this.nbMoves = plyCount;
        this.exampleMoves = Collections.emptyList(); // Default to no example moves for this constructor
    }


    public String getCode() { return code; }
    public String getName() { return name; }
    public OpeningFamily getFamily() { return family; } 
    public Optional<String> getVariation() { return variation; } 
    public int getNbMoves() { return nbMoves; } 
    public List<SanStr> getExampleMoves() { return exampleMoves; }


    // Nested AtPly class, inherits from Opening
    // The original AtPly in lila.chess.Opening was a case class extending Opening
    // For Java, direct extension and passing all fields up is one way.
    public static class AtPly extends Opening {
         private final Ply ply; // The ply at which this opening occurs

         public AtPly(String code, String name, OpeningFamily family, Optional<String> variation, int nbMoves, List<SanStr> exampleMoves, Ply ply) {
             super(code, name, family, variation, nbMoves, exampleMoves);
             this.ply = Objects.requireNonNull(ply, "Ply cannot be null for Opening.AtPly");
         }

         // Simpler constructor for AtPly if needed, matching the simpler Opening constructor
         public AtPly(String code, String name, String familyKey, String familyName, String variationName, int openingMoveCount, Ply ply) {
             super(code, name, familyKey, familyName, variationName, openingMoveCount);
             this.ply = Objects.requireNonNull(ply, "Ply cannot be null for Opening.AtPly");
         }
         
         public Ply getPly() { return ply; }
    }
}
