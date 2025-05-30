package com.example.common;

public enum PlayerTitle {
    BOT("BOT"),
    // Add other titles here
    GM("GM");

    private final String value;

    PlayerTitle(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isBot() {
        return this == BOT;
    }
}
