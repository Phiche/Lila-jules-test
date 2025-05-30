package com.example.common; // Assuming this is the target package for common module files

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// Assuming com.example.common.UserName is available from previous work
// import com.example.common.UserName; // Not needed if in the same package, but good for clarity if it were elsewhere

public final class LameName {

    private LameName() {} // Private constructor to prevent instantiation

    private static final String TITLE_PATTERN_STR = "W*(?:[NCFI1L]|I?G)";

    // Regex patterns need careful escaping for Java strings if they contain backslashes.
    // The original Scala regexes are quite complex.
    private static final Pattern CONTAINS_TITLE_REGEX = Pattern.compile(
        "(?i:" + TITLE_PATTERN_STR + "M[^a-z].*)|" +
        "(?:(?i:" + TITLE_PATTERN_STR + ")m[^a-z].*)|" +
        "(?:" + TITLE_PATTERN_STR + "M.*)|" +
        "(?i:.*[^a-z]" + TITLE_PATTERN_STR + "M)|" +
        "(?i:.*[^a-z]" + TITLE_PATTERN_STR + "M[^a-z].*)|" +
        "(?:.*[^A-Z]" + TITLE_PATTERN_STR + "M(?:[A-Z]?[^A-Z].*)?)"
    );

    private static final List<String> BASE_WORDS = Collections.unmodifiableList(Arrays.asList(
        "1488", "8814", "administrator", "asshole", "bastard", "biden", "bitch",
        "butthole", "buttsex", "cancer", "cheat", "coon", "cuck", "cunniling", "cunt", "cyka",
        "douche", "fag", "fart", "feces", "fuck", "golam", "hitler", "idiot", "jerk",
        "kanker", "kunt", "moderator", "mongool", "nazi", "nigg", "pedo", "penis",
        "pidar", "pidr", "piss", "poon", "poop", "poxyu", "pussy", "putin", "resign",
        "retard", "slut", "suicid", "trump", "vagin", "wanker", "whore",
        "xyula", "xyulo", "xyuta"
    ));

    private static final List<String> USERNAME_WORDS;
    static {
        List<String> tempUsernameWords = new ArrayList<>(BASE_WORDS);
        tempUsernameWords.addAll(Arrays.asList("lichess", "corona", "covid"));
        USERNAME_WORDS = Collections.unmodifiableList(tempUsernameWords);
    }

    private static final Pattern USERNAME_REGEX;
    private static final Pattern USERNAME_EXPLAIN_REGEX;

    private static String lameWords(List<String> list) {
        Map<Character, String> extras = new HashMap<>();
        extras.put('a', "4@"); // Added @ for 'a' as common leetspeak
        extras.put('e', "3");   // Simplified 'e'
        extras.put('g', "q96"); // Added 6 for 'g'
        extras.put('i', "l1!"); // Added ! for 'i'
        extras.put('l', "I1");
        extras.put('o', "0");   // Simplified 'o'
        extras.put('s', "5$"); // Added $ for 's'
        extras.put('t', "7");   // Added 7 for 't'
        extras.put('u', "v");
        extras.put('z', "2");

        Map<Character, String> subs = new HashMap<>();
        for (char c = 'a'; c <= 'z'; c++) {
            subs.put(c, "[" + c + Character.toUpperCase(c) + extras.getOrDefault(c, "") + "]");
        }
        // Add common number to letter substitutions explicitly if not covered by extras
        subs.put('0', "[0oO]"); // Extended '0' to match 'o' and 'O'
        subs.put('1', "[1ilIL!]"); // Extended '1'
        subs.put('3', "[3eE]");
        subs.put('4', "[4aA@]");
        subs.put('5', "[5sS$]");
        subs.put('6', "[6gG]");
        subs.put('7', "[7tT]");
        subs.put('8', "[8bB]"); // 8 can be B
        subs.put('9', "[9gG]"); // 9 can be G

        return list.stream()
            .map(word -> word.chars()
                .mapToObj(c -> (char) c)
                .map(l -> subs.getOrDefault(l, Pattern.quote(String.valueOf(l)))) // Quote non-mapped chars
                .map(l_processed -> l_processed + "+") 
                .collect(Collectors.joining()))
            .collect(Collectors.joining("|"));
    }

    static {
        String usernameWordsPatternStr = lameWords(USERNAME_WORDS);
        USERNAME_REGEX = Pattern.compile("(?i)(?:.*)(" + usernameWordsPatternStr + ")(?:.*)"); // Ensure case-insensitivity and allow surrounding chars
        USERNAME_EXPLAIN_REGEX = Pattern.compile("(" + usernameWordsPatternStr + ")"); // Capturing group for the matched word
    }
    
    private static String simplify(com.example.common.UserName name) {
        if (name == null || name.getValue() == null) {
            return "";
        }
        // Remove spaces, underscores, hyphens for simplification
        return name.getValue().toLowerCase().replaceAll("[_\\-\\s]", "");
    }

    public static boolean username(com.example.common.UserName name) {
        if (name == null) return false;
        String simplifiedName = simplify(name);
        if (simplifiedName.isEmpty()) return false;
        return USERNAME_REGEX.matcher(simplifiedName).find() || hasTitle(name.getValue());
    }

    public static boolean hasTitle(String name) {
        if (name == null) return false;
        return CONTAINS_TITLE_REGEX.matcher(name).matches();
    }

    public static Optional<String> explain(com.example.common.UserName name) {
        if (name == null || name.getValue() == null) return Optional.empty();
        if (hasTitle(name.getValue())) {
            return Optional.of("Contains a title");
        }
        String simplifiedName = simplify(name);
        if (simplifiedName.isEmpty()) return Optional.empty();
        
        Matcher matcher = USERNAME_EXPLAIN_REGEX.matcher(simplifiedName);
        if (matcher.find()) {
            // Attempt to find which original word caused the match for a clearer explanation
            // This is complex because USERNAME_EXPLAIN_REGEX is a giant OR of processed words.
            // A simpler explanation might be sufficient, or one could iterate through USERNAME_WORDS
            // and test each one individually against the simplified name with its leetspeak pattern.
            // For now, just returning the matched part.
            return Optional.of("Possibly inappropriate username due to: \"" + matcher.group(1) + "\"");
        }
        return Optional.empty();
    }
}
