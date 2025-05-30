package com.example.config;
public class Secret {
    private final String value;
    public Secret(String value) { this.value = value; }
    public String getValue() { return value; }
    @Override public String toString() { return "Secret(****)"; }
}
