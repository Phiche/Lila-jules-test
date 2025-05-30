package com.example.config;
import java.util.Optional;
public class Credentials {
    private final String user;
    private final Secret password;
    public Credentials(String user, Secret password) {
        this.user = user;
        this.password = password;
    }
    public String getUser() { return user; }
    public Secret getPassword() { return password; }
    public String show() { return user + ":" + password.getValue(); }
    public static Optional<Credentials> read(String str) {
        if (str == null) return Optional.empty();
        String[] parts = str.split(":", 2);
        if (parts.length == 2) {
            return Optional.of(new Credentials(parts[0], new Secret(parts[1])));
        }
        return Optional.empty();
    }
}
