package com.example.config;
import java.time.Duration;
public class Every {
    private final Duration value;
    public Every(Duration value) { this.value = value; }
    public Duration getValue() { return value; }
}
