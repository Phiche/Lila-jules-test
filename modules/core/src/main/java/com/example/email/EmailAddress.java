package com.example.email;

import com.example.common.Domain; // Placeholder for Domain
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EmailAddress {

    private final String value;

    private static final Pattern REGEX = Pattern.compile("(?i)^[a-z0-9.!#$&'*+/=?^_`{|}~\-]+@[a-z0-9](?:[a-z0-9-]{0,62}+(?<!-))?(?:\.[a-z0-9](?:[a-z0-9-]{0,62}+(?<!-))?)*$");
    public static final int MAX_LENGTH = 320;
    public static final Set<Domain.Lower> GMAIL_DOMAINS = Domain.Lower.from(new HashSet<>(Arrays.asList("gmail.com", "googlemail.com")));
    public static final Set<Domain.Lower> YANDEX_DOMAINS = Domain.Lower.from(new HashSet<>(Arrays.asList("yandex.com", "yandex.ru", "ya.ru", "yandex.ua", "yandex.kz", "yandex.by")));
    private static final Set<Domain.Lower> GMAIL_LIKE_NORMALIZED_DOMAINS = new HashSet<>() {{
        addAll(GMAIL_DOMAINS);
        addAll(Domain.Lower.from(new HashSet<>(Arrays.asList("protonmail.com", "protonmail.ch", "pm.me", "proton.me"))));
    }};
    public static final Pattern CLAS_ID_REGEX = Pattern.compile("^noreply\.class\.(\w{8})\.[\w-]+@lichess\.org");


    public EmailAddress(String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid email address: " + value);
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String username() {
        return value.substring(0, value.indexOf('@'));
    }

    public String conceal() {
        String[] parts = value.split("@");
        if (parts.length == 2) {
            return parts[0].substring(0, Math.min(parts[0].length(), 3)) + "*****@" + parts[1];
        }
        return value;
    }

    public NormalizedEmailAddress normalize() {
        String lower = value.toLowerCase();
        String[] parts = lower.split("@");
        if (parts.length == 2) {
            String name = parts[0];
            String domain = parts[1];
            String skipAfterPlus = name.split("\+", 2)[0];
            String normalizedName;
            if (GMAIL_LIKE_NORMALIZED_DOMAINS.contains(new Domain.Lower(domain))) {
                normalizedName = skipAfterPlus.replace(".", "");
            } else {
                normalizedName = skipAfterPlus;
            }
            return new NormalizedEmailAddress(normalizedName.isEmpty() ? lower : normalizedName + "@" + domain);
        }
        return new NormalizedEmailAddress(lower);
    }

    public Optional<Domain> domain() {
        String[] parts = value.split("@");
        if (parts.length == 2) {
            return Domain.from(parts[1].toLowerCase());
        }
        return Optional.empty();
    }

    public Optional<Pair<String, Domain>> nameAndDomain() {
        return domain().map(d -> new Pair<>(username(), d));
    }

    public boolean similarTo(EmailAddress other) {
        return this.normalize().eliminateDomainAlias().equals(other.normalize().eliminateDomainAlias());
    }
    
    public NormalizedEmailAddress eliminateDomainAlias() {
        return nameAndDomain().map(pair -> {
            String name = pair.getKey();
            Domain domain = pair.getValue();
            String newDomainStr;
            if (YANDEX_DOMAINS.contains(domain.lower())) {
                newDomainStr = "yandex.com";
            } else if (GMAIL_DOMAINS.contains(domain.lower())) {
                newDomainStr = "gmail.com";
            } else {
                newDomainStr = domain.getValue();
            }
            return new NormalizedEmailAddress(name + "@" + newDomainStr);
        }).orElse(this.normalize());
    }

    public boolean isNoReply() {
        return value.startsWith("noreply.") && value.endsWith("@lichess.org");
    }

    public boolean isBlank() {
        return value.startsWith("noreply.blanked.");
    }

    public boolean isSendable() {
        return !isNoReply() && !isBlank();
    }

    public boolean looksLikeFakeEmail() {
        return domain().map(Domain::lower).map(GMAIL_DOMAINS::contains).orElse(false) &&
               (username().chars().filter(ch -> ch == '.').count() >= 3 ||
                (username().chars().filter(ch -> ch == '.').count() == 2 &&
                 Pattern.compile("\d\.\d").matcher(username()).find()));
    }

    public static boolean isValid(String str) {
        return str != null && str.length() < MAX_LENGTH &&
               REGEX.matcher(str).matches() &&
               !str.contains("..") &&
               !str.contains(".@") &&
               !str.startsWith(".");
    }

    public static Optional<EmailAddress> from(String str) {
        return isValid(str) ? Optional.of(new EmailAddress(str)) : Optional.empty();
    }
}

// Helper Pair class
class Pair<K, V> {
    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
