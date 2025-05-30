package com.example.common;

public class TourId {
    private final String value;
    public TourId(String value) { this.value = value; }
    public String getValue() { return value; }
     @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; TourId tourId = (TourId) o; return value.equals(tourId.value); }
    @Override public int hashCode() { return value.hashCode(); }
}
