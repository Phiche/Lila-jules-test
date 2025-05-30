package com.example.common.http;
import java.util.Objects;
public class Bearer {
    private final String value; // The token itself
    public Bearer(String value) { this.value = Objects.requireNonNull(value); }
    public String getValue() { return value; }
}
