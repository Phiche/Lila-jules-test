package com.example.common;
import java.util.Collections;
import java.util.Map;

// Placeholder for chess.format.pgn.Tags
public class Tags {
    private final Map<String, String> values;
    public Tags(Map<String, String> values) {
        this.values = values == null ? Collections.emptyMap() : Collections.unmodifiableMap(values);
    }
    public Map<String, String> getValues() { return values; }
    public static Tags empty() { return new Tags(Collections.emptyMap()); }
}
