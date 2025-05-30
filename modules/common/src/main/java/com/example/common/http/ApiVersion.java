package com.example.common.http;
import java.util.Objects;
import java.util.Optional;
public class ApiVersion {
    private final int value;
    public ApiVersion(int value) { this.value = value; }
    public int getValue() { return value; }
    public static Optional<ApiVersion> from(Optional<Integer> versionIntOpt) {
        // Ensure that if versionIntOpt is null, it doesn't cause NPE
        return versionIntOpt == null ? Optional.empty() : versionIntOpt.map(ApiVersion::new);
    }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; ApiVersion that = (ApiVersion) o; return value == that.value; }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return String.valueOf(value); }
}
