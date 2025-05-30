package com.example.lilaism;

public class LilaInvalid extends RuntimeException implements LilaException {
    private final String message;

    public LilaInvalid(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
