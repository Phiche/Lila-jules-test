package com.example.common.http;
import java.util.Objects;
import java.util.Optional; // Added for the from(Optional<String>) method

public class UserAgent {
    private final String value;
    public UserAgent(String value) { this.value = Objects.requireNonNull(value); }
    public String getValue() { return value; }
    public static UserAgent from(String userAgentString) { // Helper to match Scala's UserAgent.from
        return new UserAgent(userAgentString == null ? "" : userAgentString);
    }
    public static UserAgent from(java.util.Optional<String> userAgentStringOpt) {
        return new UserAgent(userAgentStringOpt.orElse(""));
    }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; UserAgent userAgent = (UserAgent) o; return value.equals(userAgent.value); }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
