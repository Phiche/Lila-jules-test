package com.example.lilaism;

public class LilaExceptionProvider {
    public static LilaException create(String message) {
        // The original anonymous class syntax was incorrect.
        // LilaInvalid already extends RuntimeException and implements LilaException,
        // so it can be used here to fulfill the apparent contract.
        return new LilaInvalid(message);
    }
}
