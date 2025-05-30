package com.example.chess;

import java.util.Objects;

// Placeholder for chess.opening.OpeningFamily
public class OpeningFamily {
    private final String key; // Example: "A00"
    private final String name; // Example: "Anderssen's Opening"

    public OpeningFamily(String key, String name) {
        this.key = Objects.requireNonNull(key);
        this.name = Objects.requireNonNull(name);
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpeningFamily that = (OpeningFamily) o;
        return key.equals(that.key) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, name);
    }

    @Override
    public String toString() {
        return name + " (" + key + ")";
    }
}
