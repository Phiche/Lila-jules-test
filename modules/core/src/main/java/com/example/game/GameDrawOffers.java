package com.example.game;

import com.example.chess.Color;
import com.example.chess.Ply;
import java.util.Collections;
import java.util.Comparator; // Added for Ply.PLY_COMPARATOR assumption
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameDrawOffers {
    private final Set<Ply> white;
    private final Set<Ply> black;

    public static final GameDrawOffers EMPTY = new GameDrawOffers(Collections.emptySet(), Collections.emptySet());

    public GameDrawOffers(Set<Ply> white, Set<Ply> black) {
        this.white = white == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(white));
        this.black = black == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(black));
    }

    public Set<Ply> getWhite() { return white; }
    public Set<Ply> getBlack() { return black; }

    public boolean isEmpty() {
        return white.isEmpty() && black.isEmpty();
    }

    public Optional<Ply> lastBy(Color color) {
        Set<Ply> offers = (color == Color.WHITE) ? white : black;
        // Assuming Ply will have a static PLY_COMPARATOR or Ply implements Comparable<Ply>
        // For now, let's assume it's Comparable<Ply> for simplicity.
        // If Ply.PLY_COMPARATOR is a static field, it would be Ply.PLY_COMPARATOR
        // If Ply implements Comparable<Ply>, then Comparator.naturalOrder() or Ply::compareTo can be used.
        // Let's use a placeholder comparator based on Ply's int value for now.
        return offers.stream().max(Comparator.comparingInt(Ply::getValue));
    }

    public GameDrawOffers add(Color color, Ply ply) {
        if (color == Color.WHITE) {
            Set<Ply> newWhite = new HashSet<>(white);
            newWhite.add(ply);
            return new GameDrawOffers(newWhite, black);
        } else {
            Set<Ply> newBlack = new HashSet<>(black);
            newBlack.add(ply);
            return new GameDrawOffers(white, newBlack);
        }
    }

    public Set<Ply> normalize(Color color) {
        Set<Ply> offers = (color == Color.WHITE) ? white : black;
        return offers.stream()
                         .map(ply -> ply.getTurn() == color ? ply.next() : ply) // Assumes Ply.next() exists
                         .collect(Collectors.toSet());
    }

    public Set<Ply> getNormalizedPlies() {
        return Stream.concat(normalize(Color.WHITE).stream(), normalize(Color.BLACK).stream())
                     .collect(Collectors.toSet());
    }
}
