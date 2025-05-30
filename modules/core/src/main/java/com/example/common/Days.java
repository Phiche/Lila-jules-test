package com.example.common;

public class Days {
    private final int value;

    public Days(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Days days = (Days) o;
        return value == days.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
