package com.example.common;

public class SwissId {
    private final String value;
    public SwissId(String value) { this.value = value; }
    public String getValue() { return value; }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; SwissId swissId = (SwissId) o; return value.equals(swissId.value); }
    @Override public int hashCode() { return value.hashCode(); }
}
