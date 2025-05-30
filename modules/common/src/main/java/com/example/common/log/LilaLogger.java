package com.example.common.log;

// Placeholder for lila.log.Logger
// In a real application, this would likely be an SLF4J logger or similar.
public interface LilaLogger {
    void info(String message);
    void warn(String message);
    void warn(String message, Throwable t);
    // Add other logging levels (debug, error) as needed
}
