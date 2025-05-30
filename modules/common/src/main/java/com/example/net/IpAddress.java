package com.example.net;
import java.util.Objects;
public class IpAddress {
    private final String value;
    public IpAddress(String value) {
        // Consider adding validation for IP address format if needed,
        // but original was a simple value class.
        this.value = Objects.requireNonNull(value);
    }
    public String getValue() { return value; }
    public static IpAddress unchecked(String ip) {
        return new IpAddress(ip == null ? "" : ip); // Handle null input for unchecked
    }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; IpAddress ipAddress = (IpAddress) o; return value.equals(ipAddress.value); }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
