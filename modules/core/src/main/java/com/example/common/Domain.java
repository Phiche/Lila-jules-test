package com.example.common;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// Placeholder for Domain class
public class Domain {
    private final String value;

    public Domain(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Lower lower() {
        return new Lower(value.toLowerCase());
    }

    public static Optional<Domain> from(String str) {
        // Add validation if necessary
        return Optional.of(new Domain(str));
    }

    public static class Lower {
        private final String value;

        public Lower(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Set<Lower> from(Set<String> domains) {
            return domains.stream().map(Lower::new).collect(Collectors.toSet());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Lower lower = (Lower) o;
            return value.equals(lower.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }
}
