package com.example.perf;

// Placeholder for PerfKey
// Assuming it might take variant and speed or some other identifiers.
// This will be fleshed out when PerfKey's actual usage and definition are clearer.
public class PerfKey {
    private final String key;

    public PerfKey(String key) {
        this.key = key;
    }

    // Constructor for Variant and Speed, assuming Variant and Speed enums/classes exist
    // public PerfKey(com.example.chess.Variant variant, com.example.chess.Speed speed) {
    //     this.key = variant.toString().toLowerCase() + "_" + speed.toString().toLowerCase();
    // }


    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
