package com.example.email;

import java.util.Optional;

public class UserStrOrEmail {
    private final String value;

    public UserStrOrEmail(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public UserIdOrEmail normalize() {
        Optional<EmailAddress> email = EmailAddress.from(value);
        return email.map(e -> new UserIdOrEmail(e.normalize().getValue()))
                        .orElse(new UserIdOrEmail(value.toLowerCase()));
    }
}
