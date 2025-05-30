package com.example.common.play;
import java.util.Locale; // Using Java's Locale as a potential backing
import java.util.Objects;
public class PlayLang {
    private final String language;
    private final String country; // Optional in play.api.i18n.Lang but often used

    public PlayLang(String language, String country) {
        this.language = Objects.requireNonNull(language);
        this.country = country; // Country can be null
    }
    public PlayLang(String language) {
        this(language, null);
    }
    public String language() { return language; }
    public String getLanguage() { return language; } // Common getter style
    public String country() { return country; }
    public String getCountry() { return country; } // Common getter style
    public static PlayLang fromLocale(Locale locale) {
        return new PlayLang(locale.getLanguage(), locale.getCountry());
    }
    public Locale toLocale() { // Renamed from toLocale to avoid conflict with potential future field
        if (country != null && !country.isEmpty()) {
            return new Locale(language, country);
        }
        return new Locale(language);
    }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; PlayLang playLang = (PlayLang) o; return language.equals(playLang.language) && Objects.equals(country, playLang.country); }
    @Override public int hashCode() { return Objects.hash(language, country); }
    @Override public String toString() { return language + (country != null && !country.isEmpty() ? "-" + country : ""); }
}
