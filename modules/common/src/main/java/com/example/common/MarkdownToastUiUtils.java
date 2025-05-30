package com.example.common;

import com.example.data.Html;
import com.example.data.Markdown;

import java.util.regex.Matcher; // Will be needed by methods
import java.util.regex.Pattern; // Will be needed by methods

public final class MarkdownToastUiUtils {

    private MarkdownToastUiUtils() {
        // Private constructor to prevent instantiation
    }

    // --- Nested LatexUtils class ---
    private static final class LatexUtils {
        private LatexUtils() {
            // Private constructor to prevent instantiation
        }

        // Regex to match patterns like $$content$$ or $$$content$$$
        // \\$ matches a single $
        // {2,} matches 2 or more occurrences of the preceding token ($)
        // ([^$]++) captures one or more characters that are not $ (reluctant quantifier)
        // The outer \\$\{2,\} matches the closing $$ or $$$ etc.
        private static final Pattern LATEX_REGEX = Pattern.compile("\\${2,}([^$]++)\\${2,}");

        public static Markdown removeFrom(Markdown markdown) {
            if (markdown == null || markdown.getValue() == null) {
                return new Markdown("");
            }
            String originalText = markdown.getValue();
            // Replacement string: "$$ " + captured content + " $$"
            // Matcher.quoteReplacement is used to ensure that any special characters in group 1
            // are treated literally in the replacement string, though for this specific regex,
            // group 1 ([^$]++) should not contain $ or \.
            // Using a lambda for replacement to have more control if needed,
            // but simple replaceAll with string is also fine here.
            Matcher matcher = LATEX_REGEX.matcher(originalText);
            String processedText = matcher.replaceAll("\\$\\$ $1 \\$\\$");

            return new Markdown(processedText);
        }
    }

    // --- Public static method using LatexUtils ---
    public static Markdown latexRemoveFrom(Markdown markdown) {
        return LatexUtils.removeFrom(markdown);
    }

    // Other methods and nested classes will be added here by subsequent instructions.
}
