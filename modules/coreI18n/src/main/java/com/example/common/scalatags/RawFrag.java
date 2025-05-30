package com.example.common.scalatags;
// Placeholder for scalatags.Text.RawFrag
// Represents a raw HTML fragment that should not be escaped.
public class RawFrag {
    private final String value;
    public RawFrag(String value) { this.value = value; }
    public String getValue() { return value; }
    @Override public String toString() { return value; }
}
