package com.example.common;

import com.example.common.play.PlayLang; // Placeholder for play.api.i18n.Lang
import com.example.data.Ints;       // From lila.core.data
import com.example.data.Strings;    // From lila.core.data
import com.example.net.IpAddress;   // Placeholder for lila.core.net.IpAddress

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects; // For Objects::nonNull
import java.util.regex.Pattern; // For Pattern.quote
import java.util.stream.Collectors;

public final class IsoUtils {

    private IsoUtils() {}

    /**
     * Creates an Iso for String <-> Strings (List<String> wrapper).
     * @param separator The delimiter string.
     * @return An Iso instance.
     */
    public static Iso<String, Strings> strings(String separator) {
        final String finalSeparator = separator; // Effectively final for use in lambda/anonymous class
        return new Iso<String, Strings>() {
            @Override
            public Strings to(String s) {
                if (s == null || s.isEmpty()) {
                    return new Strings(Collections.emptyList());
                }
                // Split and trim, filter out empty strings if original did (nonEmpty was not in this specific Scala snippet but often is)
                List<String> list = Arrays.stream(s.split(Pattern.quote(finalSeparator)))
                                          .map(String::trim)
                                          // .filter(str -> !str.isEmpty()) // Optional: if empty strings between separators should be ignored
                                          .collect(Collectors.toList());
                return new Strings(list);
            }

            @Override
            public String from(Strings strings) {
                if (strings == null || strings.getValue() == null) {
                    return "";
                }
                return String.join(finalSeparator, strings.getValue());
            }
        };
    }

    /**
     * Creates an Iso for String <-> Ints (List<Integer> wrapper).
     * @param separator The delimiter string.
     * @return An Iso instance.
     */
    public static Iso<String, Ints> ints(String separator) {
        final String finalSeparator = separator; // Effectively final
        return new Iso<String, Ints>() {
            @Override
            public Ints to(String s) {
                if (s == null || s.isEmpty()) {
                    return new Ints(Collections.emptyList());
                }
                List<Integer> list = Arrays.stream(s.split(Pattern.quote(finalSeparator)))
                                           .map(String::trim)
                                           .map(str -> {
                                               try {
                                                   return Integer.parseInt(str);
                                               } catch (NumberFormatException e) {
                                                   // Log error or handle as per application requirements
                                                   // System.err.println("Failed to parse int: " + str);
                                                   return null;
                                               }
                                           })
                                           .filter(Objects::nonNull) // Filter out non-parseable
                                           .collect(Collectors.toList());
                return new Ints(list);
            }

            @Override
            public String from(Ints ints) {
                if (ints == null || ints.getValue() == null) {
                    return "";
                }
                return ints.getValue().stream()
                           .map(String::valueOf)
                           .collect(Collectors.joining(finalSeparator));
            }
        };
    }

    /**
     * Iso for String <-> IpAddress.
     */
    public static final Iso<String, IpAddress> IP_ADDRESS_ISO = new Iso<String, IpAddress>() {
        @Override
        public IpAddress to(String s) {
            // IpAddress.unchecked handles null by creating an IpAddress with ""
            return IpAddress.unchecked(s);
        }

        @Override
        public String from(IpAddress ipAddress) {
            // Assuming IpAddress constructor ensures value is not null.
            // If IpAddress itself can be null, add a null check.
            return ipAddress == null ? "" : ipAddress.getValue();
        }
    };

    /**
     * Iso for String <-> PlayLang (placeholder for play.api.i18n.Lang).
     */
    public static final Iso<String, PlayLang> PLAY_LANG_ISO = new Iso<String, PlayLang>() {
        @Override
        public PlayLang to(String s) {
            if (s == null || s.trim().isEmpty()) {
                 // Consider returning I18nConstants.DEFAULT_LANG or throwing
                throw new IllegalArgumentException("Lang string cannot be null or empty for Iso conversion.");
            }
            String[] parts = s.split("[-_]", 2); // Split only on the first occurrence
            if (parts.length == 1) {
                return new PlayLang(parts[0].toLowerCase());
            } else if (parts.length == 2) {
                return new PlayLang(parts[0].toLowerCase(), parts[1].toUpperCase());
            }
            // This case should ideally not be reached if split limit is 2 and input is not empty.
            // However, as a fallback for unexpected formats:
            throw new IllegalArgumentException("Invalid lang string format: " + s);
        }

        @Override
        public String from(PlayLang lang) {
            if (lang == null) {
                // Consider returning I18nConstants.DEFAULT_LANG.toString() or throwing
                throw new IllegalArgumentException("PlayLang cannot be null for Iso conversion.");
            }
            return lang.toString(); // Assumes PlayLang.toString() gives "lang-COUNTRY" or "lang"
        }
    };
}
