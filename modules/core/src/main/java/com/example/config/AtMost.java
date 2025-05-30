package com.example.config;
import java.time.Duration;
public class AtMost {
    private final Duration value;
    public AtMost(Duration value) { this.value = value; }
    public Duration getValue() { return value; }
}
