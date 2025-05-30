package com.example.common;

public class GamePlayerId {
    private final String value;
    public GamePlayerId(String value) { this.value = value; }
    public String getValue() { return value; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePlayerId that = (GamePlayerId) o;
        return value.equals(that.value);
    }
    @Override
    public int hashCode() { return value.hashCode(); }
}
