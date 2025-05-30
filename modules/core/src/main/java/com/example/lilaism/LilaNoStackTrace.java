package com.example.lilaism;

public class LilaNoStackTrace extends RuntimeException implements LilaException {
    private final String message;

    public LilaNoStackTrace(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this; // No stack trace
    }
}
