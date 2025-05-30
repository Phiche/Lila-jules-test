package com.example.common.http;

import com.example.net.IpAddress; // Placeholder
import jakarta.servlet.http.HttpServletRequest; // Using Jakarta EE 9+
import java.util.Arrays;
import java.util.Collections; // For Collections.unmodifiableList
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders; // For standard header names

public final class HTTPRequestUtils {

    private HTTPRequestUtils() {}

    public static boolean isXhr(HttpServletRequest req) {
        if (req == null) return false;
        return "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));
    }

    public static boolean isSynchronousHttp(HttpServletRequest req) {
        return !isXhr(req);
    }

    public static boolean isSafe(HttpServletRequest req) {
        if (req == null) return true; // Or false, depending on desired default for null req
        String method = req.getMethod().toUpperCase();
        return "GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method);
    }

    public static boolean isUnsafe(HttpServletRequest req) {
        return !isSafe(req);
    }

    public static Optional<String> getHeader(HttpServletRequest req, String headerName) {
        if (req == null) return Optional.empty();
        return Optional.ofNullable(req.getHeader(headerName));
    }

    public static Optional<String> origin(HttpServletRequest req) {
        return getHeader(req, HttpHeaders.ORIGIN);
    }

    public static Optional<String> referer(HttpServletRequest req) {
        return getHeader(req, HttpHeaders.REFERER);
    }

    private static final List<String> APP_ORIGINS = Collections.unmodifiableList(Arrays.asList(
        "capacitor://localhost", // ios
        "ionic://localhost",     // ios
        "http://localhost"       // android/dev/flutter
    ));

    public static Optional<String> appOrigin(HttpServletRequest req) {
        return origin(req).filter(reqOrigin ->
            APP_ORIGINS.stream().anyMatch(appOrigin ->
                reqOrigin.equals(appOrigin) || reqOrigin.startsWith(appOrigin + ":") // Check for port numbers
            )
        );
    }

    public static UserAgent userAgent(HttpServletRequest req) {
        return UserAgent.from(getHeader(req, HttpHeaders.USER_AGENT));
    }

    public static boolean isApi(HttpServletRequest req) {
        if (req == null || req.getRequestURI() == null) return false;
        return req.getRequestURI().startsWith("/api/");
    }

    public static boolean isApiOrApp(HttpServletRequest req) {
        return isApi(req) || appOrigin(req).isPresent();
    }

    public static boolean isAssets(HttpServletRequest req) {
        if (req == null || req.getRequestURI() == null) return false;
        return req.getRequestURI().startsWith("/assets/");
    }

    public static IpAddress ipAddress(HttpServletRequest req) {
        if (req == null) return IpAddress.unchecked("127.0.0.1"); // Default for null request

        String xForwardedFor = req.getHeader("X-Forwarded-For");
        String remoteAddr;

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can be a comma-separated list of IPs. The first one is the original client.
            remoteAddr = xForwardedFor.split(",")[0].trim();
        } else {
            remoteAddr = req.getRemoteAddr();
        }

        if (remoteAddr == null) {
            return IpAddress.unchecked("127.0.0.1"); // Should not happen if req is not null
        }
        // Strip scope id if present (e.g., %eth0)
        return IpAddress.unchecked(remoteAddr.split("%")[0]);
    }

    // trueish logic helper
    private static boolean trueish(String value) {
        if (value == null) return false;
        String lowerVal = value.toLowerCase();
        return "1".equals(lowerVal) || "true".equals(lowerVal) || "on".equals(lowerVal) || "yes".equals(lowerVal);
    }

    public static boolean isKid(HttpServletRequest req) {
        return getHeader(req, "X-Lichess-KidMode").map(HTTPRequestUtils::trueish).orElse(false);
    }

    // More methods (UaMatcher, crawlers, etc.) to be added in next step


    // --- User-Agent Specific Matchers ---
    public static final UaMatcher IS_CHROME_96_PLUS = new UaMatcher("Chrome/(?:\\d{3,}|9[6-9])");
    public static final UaMatcher IS_CHROME_113_PLUS = new UaMatcher("Chrome/(?:11[3-9]|1[2-9]\\d)");
    public static final UaMatcher IS_FIREFOX_119_PLUS = new UaMatcher("Firefox/(?:119|1[2-9]\\d)");
    public static final UaMatcher IS_MOBILE_BROWSER = new UaMatcher("(?i)iphone|ipad|ipod|android.+mobile");
    // Note: isLichessMobile(UserAgent) should be static here or on UserAgent class
    public static boolean isLichessMobile(UserAgent ua) {
        return ua != null && ua.getValue() != null && ua.getValue().startsWith("Lichess Mobile/");
    }
    public static boolean isLichessMobile(HttpServletRequest req) {
        return isLichessMobile(userAgent(req));
    }

    public static boolean isLichobile(HttpServletRequest req) { // Assuming Lichobile is a distinct app
        UserAgent ua = userAgent(req);
        return ua != null && ua.getValue() != null && ua.getValue().contains("Lichobile/");
    }

    public static boolean isLichobileDev(HttpServletRequest req) {
        return isLichobile(req) || (appOrigin(req).isPresent() && !isLichessMobile(req));
    }

    public static final UaMatcher IS_ANDROID = new UaMatcher("Android");
    public static boolean isAndroid(HttpServletRequest req) {
        return IS_ANDROID.matches(userAgent(req));
    }

    public static boolean isLitools(HttpServletRequest req) {
        return userAgent(req).equals(new UserAgent("litools")); // Exact match
    }

    // --- Crawler Detection ---
    private static final UaMatcher CRAWLER_MATCHER = new UaMatcher(
        // spiders/crawlers
        "Googlebot|GoogleOther|AdsBot|Google-Read-Aloud|bingbot|BingPreview|facebookexternalhit|meta-externalagent|SemrushBot|AhrefsBot|PetalBot|Applebot|YandexBot|YandexAdNet|YandexImages|Twitterbot|Baiduspider|Amazonbot|Bytespider|yacybot|ImagesiftBot|ChatGLM-Spider|YisouSpider|Yeti/|DataForSeoBot" +
        // apps and servers that load previews
        "|Discordbot|WhatsApp" +
        // http libs
        "|HeadlessChrome|okhttp|axios|undici|wget|curl|python-requests|aiohttp|commons-httpclient|python-urllib|python-httpx|Nessus|imroc/req"
    );

    public static Crawler isCrawler(HttpServletRequest req) {
        return new Crawler(CRAWLER_MATCHER.matches(userAgent(req)));
    }

    private static final UaMatcher IMAGE_PREVIEW_CRAWLER_MATCHER = new UaMatcher(
        "BingPreview|Discordbot|WhatsApp"
    );

    public static Crawler isImagePreviewCrawler(HttpServletRequest req) {
         return new Crawler(IMAGE_PREVIEW_CRAWLER_MATCHER.matches(userAgent(req)));
    }

    public static boolean uaMatches(HttpServletRequest req, Pattern regex) {
        if (regex == null) return false;
        UserAgent ua = userAgent(req);
        return ua != null && ua.getValue() != null && regex.matcher(ua.getValue()).find();
    }

    // Overload for UaMatcher if preferred
    public static boolean uaMatches(HttpServletRequest req, UaMatcher uaMatcher) {
        if (uaMatcher == null) return false;
        return uaMatcher.matches(userAgent(req));
    }


    // --- Other Request Properties ---
    public static boolean isFishnet(HttpServletRequest req) {
        if (req == null || req.getRequestURI() == null) return false;
        return req.getRequestURI().startsWith("/fishnet/");
    }

    public static boolean isHuman(HttpServletRequest req) {
        return isCrawler(req).no() && !isFishnet(req);
    }

    private static final Pattern FILE_EXTENSION_REGEX = Pattern.compile("\\.(?<!^\\.)[a-zA-Z0-9]{2,4}$");

    public static boolean hasFileExtension(HttpServletRequest req) {
        if (req == null || req.getRequestURI() == null) return false;
        return FILE_EXTENSION_REGEX.matcher(req.getRequestURI()).find();
    }

    public static String print(HttpServletRequest req) {
        if (req == null) return "null request";
        return printReq(req) + " " + printClient(req);
    }

    public static String printReq(HttpServletRequest req) {
        if (req == null) return "null request details";
        String domain = req.getServerName() != null ? req.getServerName() : "";
        String uri = req.getRequestURI() != null ? req.getRequestURI() : "";
        return req.getMethod() + " " + domain + uri +
               (req.getQueryString() != null ? "?" + req.getQueryString() : "");
    }

    public static String printClient(HttpServletRequest req) {
        if (req == null) return "null client details";
        return ipAddress(req).getValue() +
               " origin:" + origin(req).orElse("~") +
               " referer:" + referer(req).orElse("~") +
               " ua:" + userAgent(req).getValue();
    }

    // --- Authorization and Content Type ---

    public static Optional<Bearer> bearer(HttpServletRequest req) {
        return getHeader(req, HttpHeaders.AUTHORIZATION).filter(auth -> auth.startsWith("Bearer "))
                .map(auth -> new Bearer(auth.substring("Bearer ".length())));
    }

    public static boolean isOAuth(HttpServletRequest req) {
        return bearer(req).isPresent();
    }

    private static final String WEB_XHR_ACCEPTS = "application/web.lichess+json";

    public static boolean startsWithLichobileAccepts(String acceptHeader) {
        return acceptHeader != null && acceptHeader.startsWith("application/vnd.lichess.v");
    }

    public static Optional<String> accepts(HttpServletRequest req) {
        return getHeader(req, HttpHeaders.ACCEPT);
    }

    public static boolean acceptsNdJson(HttpServletRequest req) {
        return accepts(req).map(a -> a.equals("application/x-ndjson")).orElse(false);
    }

    public static boolean acceptsJson(HttpServletRequest req) {
        return accepts(req).map(a ->
            WEB_XHR_ACCEPTS.equals(a) || a.startsWith("application/json") || startsWithLichobileAccepts(a)
        ).orElse(false);
    }

    public static boolean acceptsCsv(HttpServletRequest req) {
        return accepts(req).map(a -> a.equals("text/csv")).orElse(false);
    }

    public static boolean isEventSource(HttpServletRequest req) {
        return accepts(req).map(a -> a.equals("text/event-stream")).orElse(false);
    }

    public static boolean isProgrammatic(HttpServletRequest req) {
        return !isSynchronousHttp(req) || isFishnet(req) || isApi(req) ||
               accepts(req).map(HTTPRequestUtils::startsWithLichobileAccepts).orElse(false);
    }

    /**
     * Placeholder for Play-specific Router.Attrs.ActionName.
     * In Spring, this might be obtained via AOP or other means.
     */
    public static String getActionName(HttpServletRequest req) {
        if (req == null) return "NoHandler";
        // In Play, this was: req.attrs.get(Router.Attrs.ActionName).getOrElse("NoHandler")
        // No direct equivalent for arbitrary attributes like this in standard HttpServletRequest.
        // Could be stored as a request attribute if set by a filter/interceptor.
        Object actionNameAttr = req.getAttribute("ACTION_NAME"); // Example attribute name
        return actionNameAttr != null ? actionNameAttr.toString() : "NoHandler";
    }

    private static final Pattern LICHOBILE_VERSION_HEADER_PATTERN = Pattern.compile("application/vnd\\.lichess\\.v(\\d+)\\+json");

    public static Optional<ApiVersion> apiVersion(HttpServletRequest req) {
        return accepts(req).flatMap(acceptHeader -> {
            Matcher matcher = LICHOBILE_VERSION_HEADER_PATTERN.matcher(acceptHeader);
            if (matcher.matches()) {
                try {
                    return Optional.of(new ApiVersion(Integer.parseInt(matcher.group(1))));
                } catch (NumberFormatException e) {
                    // Log error if needed: logger.warn("Failed to parse API version from accept header: {}", acceptHeader, e);
                    return Optional.empty();
                }
            }
            return Optional.empty();
        });
    }

    // --- Specific Path Checkers ---
    private static boolean pathMatches(HttpServletRequest req, String exactPath) {
        if (req == null || req.getRequestURI() == null) return false;
        return req.getRequestURI().equals(exactPath);
    }

    private static boolean pathStartsWith(HttpServletRequest req, String prefix) {
        if (req == null || req.getRequestURI() == null) return false;
        return req.getRequestURI().startsWith(prefix);
    }

    private static boolean pathMatchesRegex(HttpServletRequest req, Pattern pattern) {
        if (req == null || req.getRequestURI() == null || pattern == null) return false;
        return pattern.matcher(req.getRequestURI()).matches();
    }

    // Example regex patterns, these need to be defined as static final Pattern fields if used frequently
    private static final Pattern GAME_EXPORT_REGEX = Pattern.compile("^/@/[\\w-]+/download$");
    private static final Pattern GAMES_USER_EXPORT_REGEX = Pattern.compile("^/(?:api/games/user|games/export)/[\\w-]+(?:/.*)?$"); // Non-capturing group
    private static final Pattern STUDY_EXPORT_REGEX = Pattern.compile("^/study/by/[\\w-]+/export\\.pgn$");


    public static boolean isDataDump(HttpServletRequest req) {
        return pathMatches(req, "/account/personal-data");
    }

    public static boolean isAppeal(HttpServletRequest req) {
        return pathStartsWith(req, "/appeal");
    }

    public static boolean isGameExport(HttpServletRequest req) {
        if (req == null || req.getRequestURI() == null) return false;
        String path = req.getRequestURI();
        return GAME_EXPORT_REGEX.matcher(path).matches() || GAMES_USER_EXPORT_REGEX.matcher(path).matches();
    }

    public static boolean isStudyExport(HttpServletRequest req) {
        return pathMatchesRegex(req, STUDY_EXPORT_REGEX);
    }

    public static boolean isAccountClose(HttpServletRequest req) {
        if (req == null || req.getRequestURI() == null) return false;
        String path = req.getRequestURI();
        return "/account/close".equals(path) || "/account/delete".equals(path);
    }

    public static boolean isClosedLoginPath(HttpServletRequest req) {
        return isDataDump(req) || isAppeal(req) || isStudyExport(req) || isGameExport(req) || isAccountClose(req);
    }

    public static String getClientName(HttpServletRequest req) {
        if (isXhr(req)) { // isXhr already handles null req
            return apiVersion(req).map(v -> "lichobile/" + v.getValue()).orElse("xhr");
        } else if (isLichessMobile(req)) { // isLichessMobile handles null req
            return "mobile";
        } else if (isCrawler(req).isCrawler()) { // isCrawler handles null req
            return "crawler";
        }
        return "browser";
    }

    public static Optional<String> getQueryStringParam(HttpServletRequest req, String name) {
        if (req == null || name == null) return Optional.empty();
        // req.queryString().get(name).flatMap(_.headOption).filter(_.nonEmpty)
        String[] params = req.getParameterValues(name);
        if (params != null && params.length > 0 && params[0] != null && !params[0].isEmpty()) {
            return Optional.of(params[0]);
        }
        return Optional.empty();
    }

    public static boolean looksLikeLichessBot(HttpServletRequest req) {
        UserAgent ua = userAgent(req); // userAgent handles null req
        return ua.getValue().startsWith("lichess-bot/") ||
               ua.getValue().startsWith("maia-bot/");
    }

    public static boolean isNginxWhitelist(HttpServletRequest req) {
        return getHeader(req, "X-Ip-Tier").flatMap(val -> { // getHeader handles null req
            try {
                return Optional.of(Integer.parseInt(val));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }).map(tier -> tier > 1).orElse(false);
    }
}
