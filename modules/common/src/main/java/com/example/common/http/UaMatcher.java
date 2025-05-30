package com.example.common.http;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException; // For error handling

/**
 * Helper class to store a compiled regex pattern for User-Agent matching.
 */
public class UaMatcher {
    private final Pattern pattern;
    private final String regexString; // Store for debugging or if needed

    public UaMatcher(String regexString) {
        this.regexString = regexString;
        Pattern compiledPattern;
        try {
            compiledPattern = Pattern.compile(regexString);
        } catch (PatternSyntaxException e) {
            // Log or handle the error appropriately in a real application
            System.err.println("Error compiling regex for UaMatcher: " + regexString + " - " + e.getMessage());
            // Fallback to a pattern that matches nothing, or rethrow
            compiledPattern = Pattern.compile("a^"); // Matches nothing
        }
        this.pattern = compiledPattern;
    }

    public boolean matches(UserAgent userAgent) {
        if (userAgent == null || userAgent.getValue() == null) {
            return false;
        }
        // Check for the "matches nothing" pattern if compilation failed
        if ("a^".equals(pattern.pattern())) { 
            return false;
        }
        return pattern.matcher(userAgent.getValue()).find();
    }

    public String getRegexString() {
        return regexString;
    }
}
