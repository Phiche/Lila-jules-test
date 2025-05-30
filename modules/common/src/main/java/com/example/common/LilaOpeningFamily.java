package com.example.common;

import com.example.chess.Color; // Assuming com.example.chess.Color placeholder
import com.example.chess.Opening; // Assuming com.example.chess.Opening placeholder
import com.example.chess.OpeningDb; // Assuming com.example.chess.OpeningDb placeholder
import com.example.chess.OpeningFamily; // Assuming com.example.chess.OpeningFamily placeholder

import java.util.Collections;
import java.util.Comparator; // For sorting
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class LilaOpeningFamily {

    // --- Nested static Key class (from opaque type Key) ---
    public static final class Key {
        private final String value;
        public Key(String value) {
            this.value = Objects.requireNonNull(value);
        }
        public String getValue() { return value; }
        @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; Key key = (Key) o; return value.equals(key.value); }
        @Override public int hashCode() { return value.hashCode(); } // Corrected hashCode
        @Override public String toString() { return value; }
    }

    // --- Nested static Name class (from opaque type Name) ---
    public static final class Name {
        private final String value;
        public Name(String value) {
            this.value = Objects.requireNonNull(value);
        }
        public String getValue() { return value; }
        @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; Name name = (Name) o; return value.equals(name.value); }
        @Override public int hashCode() { return value.hashCode(); } // Corrected hashCode
        @Override public String toString() { return value; }
    }

    // --- Nested static AsColor class ---
    public static class AsColor {
        private final LilaOpeningFamily family;
        private final Color color;

        public AsColor(LilaOpeningFamily family, Color color) {
            this.family = Objects.requireNonNull(family);
            this.color = Objects.requireNonNull(color);
        }
        public LilaOpeningFamily getFamily() { return family; }
        public Color getColor() { return color; }
    }

    // --- LilaOpeningFamily fields ---
    private final OpeningFamily openingFamilyRef;
    private final Either<Opening, Opening> fullOrSubstitute; // Using com.example.common.Either

    // --- Static Fields and Initializer ---
    private static final Map<Key, LilaOpeningFamily> FAMILIES;
    public static final List<LilaOpeningFamily> FAMILY_LIST;

    static {
        Map<Key, LilaOpeningFamily> tempFamilies = new HashMap<>();
        List<Opening> allOpenings = OpeningDb.all();

        for (Opening currentOp : allOpenings) {
            if (currentOp == null || currentOp.getFamily() == null) continue;

            OpeningFamily familyRef = currentOp.getFamily();
            Key currentKey = new Key(familyRef.getKey());

            // An opening is "full" if its variation name is empty/not present.
            // An opening is a "substitute" if its variation name is present and non-empty.
            boolean isCurrentOpFull = currentOp.getVariation().filter(v -> !v.isEmpty()).isEmpty();

            Either<Opening, Opening> currentFullOrSub = isCurrentOpFull ?
                Either.left(currentOp) : Either.right(currentOp);

            LilaOpeningFamily newLof = new LilaOpeningFamily(familyRef, currentFullOrSub);
            LilaOpeningFamily existingLof = tempFamilies.get(currentKey);

            if (existingLof == null) {
                tempFamilies.put(currentKey, newLof);
            } else {
                // Existing entry found, apply preference logic:
                // 1. If new is "full" and existing is "substitute", replace.
                // 2. If new is "substitute" and existing is "full", keep existing.
                // 3. If both are "substitute", replace if new has fewer moves.
                // 4. If both are "full" (shouldn't happen if data is clean, but if so, keep existing or based on moves).

                boolean existingIsFull = existingLof.getFull().isPresent();

                if (isCurrentOpFull) { // newLof is "full"
                    if (!existingIsFull) { // existingLof is "substitute"
                        tempFamilies.put(currentKey, newLof); // Replace substitute with full
                    }
                    // If existingIsFull is also true, both are full. Could compare by moves or keep first one.
                    // For now, keeping the first one encountered (existingLof).
                } else { // newLof is "substitute"
                    if (existingIsFull) {
                        // Existing is "full", new is "substitute". Keep the full one.
                    } else {
                        // Both newLof and existingLof are "substitute".
                        // Compare by nbMoves: prefer the one with fewer moves.
                        Opening newSubstitute = newLof.getSubstitute().orElseThrow(); // Known to be present
                        Opening existingSubstitute = existingLof.getSubstitute().orElseThrow(); // Known to be present
                        if (newSubstitute.getNbMoves() < existingSubstitute.getNbMoves()) {
                            tempFamilies.put(currentKey, newLof);
                        }
                    }
                }
            }
        }
        FAMILIES = Collections.unmodifiableMap(tempFamilies);
        FAMILY_LIST = Collections.unmodifiableList(
            tempFamilies.values().stream()
                .sorted(Comparator.comparing(lof -> lof.getName().getValue(), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList())
        );
    }


    // --- Constructor ---
    public LilaOpeningFamily(OpeningFamily openingFamilyRef, Either<Opening, Opening> fullOrSubstitute) {
        this.openingFamilyRef = Objects.requireNonNull(openingFamilyRef);
        this.fullOrSubstitute = Objects.requireNonNull(fullOrSubstitute);
    }

    // --- Instance Methods ---
    public Name getName() {
        return new Name(openingFamilyRef.getName());
    }

    public Key getKey() {
        return new Key(openingFamilyRef.getKey());
    }

    public AsColor as(Color color) {
        return new AsColor(this, color);
    }

    public Optional<Opening> getFull() {
        return fullOrSubstitute.getLeftOption();
    }

    public Optional<Opening> getSubstitute() {
        return fullOrSubstitute.getRightOption();
    }

    public Opening getAnyOpening() {
        return fullOrSubstitute.fold(opening -> opening, opening -> opening); // Simpler way to get from Either
    }

    // --- Static Accessor Methods ---
    public static Optional<LilaOpeningFamily> get(Key key) {
        return Optional.ofNullable(FAMILIES.get(key));
    }

    public static Optional<LilaOpeningFamily> find(String keyString) {
        if (keyString == null) return Optional.empty();
        return get(new Key(keyString));
    }

    public static List<LilaOpeningFamily> getFamilyList() {
        return FAMILY_LIST;
    }

    // --- Standard Java methods ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LilaOpeningFamily that = (LilaOpeningFamily) o;
        return openingFamilyRef.getKey().equals(that.openingFamilyRef.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(openingFamilyRef.getKey());
    }

    @Override
    public String toString() {
        return "LilaOpeningFamily{" +
               "key=" + getKey().getValue() +
               ", name=" + getName().getValue() +
               ", full=" + getFull().map(Opening::getName).map(s -> "\"" + s + "\"") + // Added quotes for clarity
               ", substitute=" + getSubstitute().map(Opening::getName).map(s -> "\"" + s + "\"") + // Added quotes
               '}';
    }
}
