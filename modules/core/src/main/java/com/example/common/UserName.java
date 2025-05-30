package com.example.common;

public class UserName {
    private final String value;

    public static final UserName ANONYMOUS = new UserName("Anonymous");
    public static final UserName LICHESS = new UserName("lichess");
    // Add ANON_MOD if needed

    public UserName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public UserId getId() {
        return new UserId(this.value.toLowerCase());
    }
}
