package com.example.scalalibmodel;
import java.util.Objects;
public class LangTag {
    private final String value;
    public LangTag(String value) { this.value = value; }
    public String getValue() { return value; }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; LangTag langTag = (LangTag) o; return Objects.equals(value, langTag.value); }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value; }
}
