package com.example.config;
import java.time.Duration;
public class Delay {
    private final Duration value;
    public Delay(Duration value) { this.value = value; }
    public Duration getValue() { return value; }
}
