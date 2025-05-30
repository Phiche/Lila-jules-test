package com.example.common;

import com.example.common.misc.lpv.LinkRender;
import com.example.config.NetDomain; // Assuming NetDomain from com.example.config
import com.example.data.Html; // Assuming Html from com.example.data

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.text.StringEscapeUtils; // For HTML escaping

public final class RawHtmlUtils {

    private RawHtmlUtils() {}

    public static Html nl2br(String s) {
        if (s == null) return new Html("");
        StringBuilder sb = new StringBuilder(s.length()); // Estimate initial capacity
        int consecutiveNewlines = 0;
        for (char c : s.toCharArray()) {
            if (c == '\n') {
                consecutiveNewlines++;
                if (consecutiveNewlines < 3) { // Allow up to 2 <br> tags (for 1 or 2 newlines)
                    sb.append("<br>");
                }
            } else if (c != '\r') { // Ignore carriage returns
                consecutiveNewlines = 0; // Reset counter if not a newline
                sb.append(c);
            }
        }
        return new Html(sb.toString());
    }

    // Placeholder for scalalib.StringUtils.escapeHtmlRaw
    // Using Apache Commons Text for a robust implementation.
    private static String escapeHtmlRaw(String text) {
        if (text == null) return "";
        return StringEscapeUtils.escapeHtml4(text);
    }

    // Placeholder for scalalib.StringUtils.escapeHtmlRawInPlace
    private static void escapeHtmlRawInPlace(StringBuilder sb, char[] sArr, int start, int end) {
        if (sArr == null || start >= end || sb == null) return;
        // Appends the escaped version of the char array segment to the StringBuilder
        sb.append(escapeHtmlRaw(new String(sArr, start, end - start)));
    }

    // Regex for general URLs. Adjusted for Java Pattern.
    // Original: (?i)\b[a-z](?:ttp(?<=http)s?://(\w[-\w.~!$&';=:@]{0,100})|(?<![/@.-])(?:\w{1,15}+\.){1,3}(?:com|org|edu))([/?#][-–—\w/.~!$&'()*+,;=:#?@%]{0,300}+)?(?![/\w~$&*+=#@%])
    // Simpler version for Java, focusing on common cases, might need refinement for edge cases.
    // Group 1: Optional scheme (http/https) + domain part for http links
    // Group 2: Domain part for non-http links (e.g. lichess.org)
    // Group 3: Path/query/fragment part
    // This regex is complex and direct porting can be tricky. The one below is a common general purpose one.
    // For now, using the one from the prompt which has specific logic.
    private static final Pattern URL_PATTERN = Pattern.compile(
        "(?i)\\b[a-z](?:" +                                     // pull out first char for perf.
        "ttp(?<=http)s?://([\\w.~!$&';=:@-]{0,100})|" +     // http(s) links (simplified domain part from original)
        "(?<![/@.-])(?:[\\w]{1,15}+\\.){1,3}(?:com|org|edu))" + // "lichess.org", etc
        "([/?#][–—\\w/.~!$&'()*+,;=:#?@%]{0,300})?" +       // path, params (removed hyphen from char class start)
        "(?![\\w/~$&*+=#@%])",                                 // neg lookahead
        Pattern.CASE_INSENSITIVE
    );
    
    // Regex for user links like /@/username
    private static final Pattern USER_LINK_REGEX_PATTERN = Pattern.compile("/@/([\\w-]{2,30})"); // Simplified, removed trailing +?

    // Regex for @username mentions
    public static final Pattern AT_USERNAME_REGEX_PATTERN = Pattern.compile("@(?<![\\w@#/[]@)([\\w-]{2,30})(?![@\\w-]|\\.\\w)");

    // Regex for Markdown style links: [text](url)
    private static final Pattern MARKDOWN_LINK_REGEX = Pattern.compile("\\[([^\\]]++)]\((https?://[^)]++)\)");
    
    // Regex for common tracking parameters
    private static final Pattern TRACKING_PARAMETERS_REGEX = Pattern.compile("(?:\\?|&(?:amp;)?)(?:utm(?:_\\w+)?|gclid|gclsrc|_ga)=\\w+", Pattern.CASE_INSENSITIVE);


    public static String removeUrlTrackingParameters(String url) {
        if (url == null) return null;
        return TRACKING_PARAMETERS_REGEX.matcher(url).replaceAll("");
    }
    
    public static Html justMarkdownLinks(Html escapedHtmlInput) {
        if (escapedHtmlInput == null || escapedHtmlInput.getValue() == null) return new Html("");
        String text = escapedHtmlInput.getValue(); // Assuming input is already escaped where needed, except for the links themselves
        
        Matcher matcher = MARKDOWN_LINK_REGEX.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String linkText = matcher.group(1); // Text is already as-is from Markdown
            String href = removeUrlTrackingParameters(matcher.group(2));
            // Important: href should be escaped for attribute context if not already.
            // Assuming href from Markdown is a valid URL.
            // Link text itself should NOT be re-escaped if it's meant to be plain text from Markdown.
            String replacement = String.format("<a rel=\"nofollow noopener noreferrer\" href=\"%s\">%s</a>", 
                                               escapeHtmlRaw(href), // Ensure URL is attribute-safe
                                               linkText); // linkText is used as-is from markdown source
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement)); // Quote $ and \ in replacement
        }
        matcher.appendTail(sb);
        return new Html(sb.toString());
    }

    public static boolean hasLinks(String text) {
        if (text == null) return false;
        return URL_PATTERN.matcher(text).find() || MARKDOWN_LINK_REGEX.matcher(text).find(); // Also check markdown links
    }
    
    private static final Pattern IMGUR_REGEX = Pattern.compile("https?://(?:i\\.)?imgur\\.com/(\\w+)(?:\\.[a-z]{3,4})?(?:\\?.+)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern GIPHY_REGEX = Pattern.compile("https://(?:media\\.giphy\\.com/media/|giphy\\.com/gifs/(?:[\\w-]+-)*)(\\w+)(?:/giphy\\.gif)?(?:\\?.+)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern POSTIMG_REGEX = Pattern.compile("https?://(?:i\\.)?postimg\\.cc/([\\w/-]+)(?:\\.[a-z]{3,4})?(?:\\?.+)?", Pattern.CASE_INSENSITIVE);


    private static Optional<Html> imgUrl(String url) {
        if (url == null) return Optional.empty();
        String escapedUrl = escapeHtmlRaw(url); // Escape the URL for use in alt attribute

        Matcher imgurMatcher = IMGUR_REGEX.matcher(url);
        if (imgurMatcher.matches()) {
            return Optional.of(new Html("<img class=\"embed\" src=\"https://i.imgur.com/" + imgurMatcher.group(1) + ".jpg\" alt=\"" + escapedUrl + "\"/>"));
        }
        Matcher giphyMatcher = GIPHY_REGEX.matcher(url);
        if (giphyMatcher.matches()) {
            return Optional.of(new Html("<img class=\"embed\" src=\"https://media.giphy.com/media/" + giphyMatcher.group(1) + "/giphy.gif\" alt=\"" + escapedUrl + "\"/>"));
        }
        Matcher postimgMatcher = POSTIMG_REGEX.matcher(url);
         if (postimgMatcher.matches()) {
            // Postimg ID can contain slashes, needs to be preserved.
            return Optional.of(new Html("<img class=\"embed\" src=\"https://i.postimg.cc/" + postimgMatcher.group(1) + "\" alt=\"" + escapedUrl + "\"/>"));
            // Note: Postimg often doesn't require .jpg extension for direct image if the ID itself points to image.
            // If it needs an extension, it might be better to ensure the link itself has it or try common ones.
            // The original just used the ID.
        }
        return Optional.empty();
    }

    // expandAtUser and addLinks are more complex and will be added next.
    // adjustUrlEnd is also a helper for addLinks.

    /**
     * Helper to adjust the end of a URL match to exclude trailing punctuation.
     * Ported from Scala's adjustUrlEnd.
     */
    private static int adjustUrlEnd(char[] sArr, int urlSubStringStart, int currentEnd) {
        int last = currentEnd - 1;
        if (last < urlSubStringStart) return currentEnd; 

        // Backup original last for parenthesis check if general punctuation stripping happens
        int originalLast = last; 

        // First, strip common unambiguous trailing punctuation
        boolean changedByPunctuation = false;
        while (last >= urlSubStringStart) {
            char c = sArr[last];
            if (c == '.' || c == ',' || c == '?' || c == '!' || c == ':' || c == ';' || c == '–' || c == '—' || c == '@' || c == '\'') {
                last--;
                changedByPunctuation = true;
            } else {
                break;
            }
        }
        
        // If punctuation was stripped, or if the last char is ')' (even if not stripped by above)
        // then check parenthesis balancing.
        // The original Scala code's parenthesis logic was complex and integrated.
        // This re-evaluates from the potentially new 'last' or original 'last' if it was a ')'
        int currentEvalLast = changedByPunctuation ? last : originalLast;

        if (currentEvalLast >= urlSubStringStart && sArr[currentEvalLast] == ')') {
            int parenCnt = 0;
            // Count parens from URL start up to the current ')' we are considering stripping
            for (int i = urlSubStringStart; i < currentEvalLast; i++) {
                if (sArr[i] == '(') parenCnt++;
                else if (sArr[i] == ')') parenCnt--;
            }
            // If, before considering sArr[currentEvalLast], we have an equal or excess of '(',
            // then sArr[currentEvalLast] which is ')' might be an extraneous trailing one.
            // The original logic: `while (parenCnt += 1; parenCnt <= 0 && ...)` effectively means
            // keep stripping ')' as long as we don't end up with more '(' than ')'.
            // Let's try to simulate: if after taking the current ')', we have parenCnt-1,
            // and if this makes parenCnt (openings) less than closings (represented by negative values),
            // it's likely part of the URL.
            // This is tricky. A simpler approach: if we strip a ')' and the count of '(' up to that point
            // is less than the count of ')', it's likely a valid part of the URL.
            // The original logic is more like: keep ')' if it balances a '('.
            
            // Let's simplify: strip trailing ')' only if the count of '(' is not greater than ')' before it.
            // This is not a perfect port of the complex Scala loop.
            // The Scala loop was:
            // while (idx >= start && (sArr(idx) match {
            //   case '.' | ',' | '?' | '!' | ':' | ';' | '–' | '—' | '@' | ''' => true
            //   case '(' => parenCnt -= 1; true
            //   case ')' => parenCnt += 1; parenCnt <= 0
            //   case _   => false
            // })) idx -= 1
            // This means it continues stripping as long as the condition is true.
            // The 'parenCnt <= 0' for ')' means it strips ')' if it results in balanced or more closing parens.
            
            // Reset 'last' to where it was before general punctuation stripping if the end char is ')'
            // to re-evaluate with the integrated logic.
            last = originalLast; 
            parenCnt = 0;
            int tempLast = last; // Use a temporary index for this loop
            while (tempLast >= urlSubStringStart) {
                char c = sArr[tempLast];
                boolean stripThisChar = false;
                switch (c) {
                    case '.': case ',': case '?': case '!': case ':': case ';': case '–': case '—': case '@': case '\'':
                        stripThisChar = true;
                        break;
                    case '(':
                        parenCnt--; // This char is '(', so if we strip it, we have one less open paren
                        stripThisChar = true;
                        break;
                    case ')':
                        parenCnt++; // This char is ')', so if we strip it, we effectively remove a close paren relative to opens
                        if (parenCnt <= 0) { // If count is still zero or negative, means it's safe to strip this ')'
                            stripThisChar = true;
                        } else {
                            parenCnt--; // Don't strip, so revert the count change
                        }
                        break;
                    default:
                        // Not a character to strip based on type
                        break;
                }
                if (stripThisChar) {
                    tempLast--;
                } else {
                    break; // Stop stripping
                }
            }
            last = tempLast; // Update 'last' with the result of this stripping loop
        }
        return last + 1;
    }

    public static List<String> expandAtUser(String text, NetDomain netDomain) {
        if (text == null || netDomain == null || netDomain.getValue() == null) {
            return Collections.singletonList(text == null ? "" : text);
        }

        Matcher m = AT_USERNAME_REGEX_PATTERN.matcher(text);
        if (!m.find()) {
            return Collections.singletonList(text);
        }

        List<String> parts = new ArrayList<>();
        int lastAppendPosition = 0;
        do {
            if (m.start() > lastAppendPosition) {
                parts.add(text.substring(lastAppendPosition, m.start()));
            }
            String username = m.group(1);
            // Construct the full URL for the user profile
            parts.add("https://" + netDomain.getValue() + "/@" + username); 
            lastAppendPosition = m.end();
        } while (m.find());

        if (lastAppendPosition < text.length()) {
            parts.add(text.substring(lastAppendPosition));
        }
        return parts;
    }

    public static Html addLinks(
            String text,
            boolean expandImg,
            Optional<LinkRender> linkRenderOpt,
            NetDomain netDomain) {
        
        if (text == null || text.isEmpty()) return new Html("");
        Objects.requireNonNull(netDomain, "NetDomain cannot be null");
        Objects.requireNonNull(linkRenderOpt, "LinkRender Optional cannot be null");

        List<String> initialParts = expandAtUser(text, netDomain);
        
        StringBuilder resultHtmlBuilder = new StringBuilder();

        for (String part : initialParts) {
            // If this part is a full URL generated by expandAtUser, it should be linkified directly.
            // Otherwise, it's original text that needs URL_PATTERN matching.
            // A simple check: if part starts with "http" (from expandAtUser)
            boolean isExpandedUserLink = part.startsWith("https://" + netDomain.getValue() + "/@") || part.startsWith("http://" + netDomain.getValue() + "/@");

            if (isExpandedUserLink) {
                // This part is already a full URL to a user profile
                String username = part.substring(part.lastIndexOf('@') + 1);
                String href = escapeHtmlRaw(removeUrlTrackingParameters(part));
                String linkText = "@" + username; // Display as @username

                // Try LinkRender for user profile links if applicable
                // The path for LinkRender would be like "/@/username"
                String internalPath = "/@" + username;
                Optional<String> renderedLink = linkRenderOpt.flatMap(
                    renderer -> renderer.render(internalPath, linkText) // Pass "@username" as text? Or full URL?
                                      .map(RawFrag::getValue)
                );

                resultHtmlBuilder.append(renderedLink.orElseGet(() ->
                    String.format("<a href=\"%s\">%s</a>", href, escapeHtmlRaw(linkText))
                ));
            } else {
                // This part is original text, process with URL_PATTERN
                Matcher m = URL_PATTERN.matcher(part);
                StringBuffer sb = new StringBuffer();

                while (m.find()) {
                    char[] partChars = part.toCharArray();
                    int adjustedEnd = adjustUrlEnd(partChars, m.start(), m.end());
                    String actualMatchedUrl = part.substring(m.start(), adjustedEnd);
                    
                    String httpSchemeDomainGroup = m.group(1); // Domain from http(s)://domain
                    String genericDomainGroup = m.group(2); // Domain like lichess.org
                    // pathGroup (m.group(3)) is not explicitly used in domain check here.

                    String matchedDomainForCheck;
                    if (httpSchemeDomainGroup != null) {
                        matchedDomainForCheck = httpSchemeDomainGroup;
                    } else {
                        matchedDomainForCheck = genericDomainGroup;
                    }
                    
                    // Normalize domain for checking (e.g. remove www.) - this is complex
                    // For now, a direct comparison with netDomain.getValue()
                    boolean isTldInternal = netDomain.getValue().equalsIgnoreCase(matchedDomainForCheck);
                    
                    String urlForHref;
                    String linkTextContent = escapeHtmlRaw(actualMatchedUrl);

                    if (actualMatchedUrl.matches("(?i)^https?://.*")) {
                        urlForHref = escapeHtmlRaw(removeUrlTrackingParameters(actualMatchedUrl));
                    } else {
                        urlForHref = "http://" + escapeHtmlRaw(removeUrlTrackingParameters(actualMatchedUrl));
                    }

                    String replacement;
                    if (isTldInternal) {
                        String internalPath = actualMatchedUrl;
                        // Attempt to make it a relative path if it starts with the domain
                        if (internalPath.toLowerCase().startsWith("http://" + netDomain.getValue().toLowerCase())) {
                           internalPath = internalPath.substring(("http://" + netDomain.getValue()).length());
                        } else if (internalPath.toLowerCase().startsWith("https://" + netDomain.getValue().toLowerCase())) {
                           internalPath = internalPath.substring(("https://" + netDomain.getValue()).length());
                        } else if (internalPath.toLowerCase().startsWith(netDomain.getValue().toLowerCase())) {
                            // Case where it's just domain.com/path without scheme
                            internalPath = internalPath.substring(netDomain.getValue().length());
                        }
                        if (internalPath.isEmpty() || !internalPath.startsWith("/")) internalPath = "/" + internalPath;
                        
                        final String finalInternalPath = removeUrlTrackingParameters(internalPath);
                        final String displaytextForRenderer = netDomain.getValue() + finalInternalPath;

                        Optional<String> renderedOpt = linkRenderOpt.flatMap(
                            renderer -> renderer.render(finalInternalPath, displaytextForRenderer)
                                              .map(RawFrag::getValue)
                        );
                        
                        replacement = renderedOpt.orElseGet(() -> {
                            String hrefAttr = escapeHtmlRaw(finalInternalPath.isEmpty() ? "/" : finalInternalPath);
                            String displayLinkText;
                            Matcher userLinkMatcher = USER_LINK_REGEX_PATTERN.matcher(finalInternalPath);
                            if (userLinkMatcher.matches()) {
                                displayLinkText = "@" + userLinkMatcher.group(1);
                            } else {
                                displayLinkText = escapeHtmlRaw(netDomain.getValue() + finalInternalPath);
                            }
                            return String.format("<a href=\"%s\">%s</a>", hrefAttr, displayLinkText);
                        });
                    } else {
                        Optional<Html> imgHtml = expandImg ? imgUrl(actualMatchedUrl) : Optional.empty();
                        replacement = imgHtml.map(Html::getValue).orElseGet(() -> 
                            String.format("<a rel=\"nofollow noreferrer\" href=\"%s\" target=\"_blank\">%s</a>", 
                                          urlForHref, 
                                          linkTextContent)
                        );
                    }
                    m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
                }
                m.appendTail(sb);
                resultHtmlBuilder.append(sb.toString());
            }
        }
        return new Html(resultHtmlBuilder.toString());
    }
}
