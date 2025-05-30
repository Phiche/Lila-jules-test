package com.example.scalalibmodel;
import java.util.Objects;
public class Language {
    private final String value;
    public Language(String value) { this.value = value; }
    public String getValue() { return value; }
    public Language map(java.util.function.Function<String, String> f) { return new Language(f.apply(this.value)); }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; Language language = (Language) o; return Objects.equals(value, language.value); }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value; }
}
