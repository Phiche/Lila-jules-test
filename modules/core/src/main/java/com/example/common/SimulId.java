package com.example.common;

public class SimulId {
    private final String value;
    public SimulId(String value) { this.value = value; }
    public String getValue() { return value; }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; SimulId simulId = (SimulId) o; return value.equals(simulId.value); }
    @Override public int hashCode() { return value.hashCode(); }
}
