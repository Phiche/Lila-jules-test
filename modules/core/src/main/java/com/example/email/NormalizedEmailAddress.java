package com.example.email;

import com.example.common.Domain; // Assuming Domain is in this package
import java.util.Optional;

public class NormalizedEmailAddress {
    private final String value;

    public NormalizedEmailAddress(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // Added from extension in Scala
    public String username() {
        return value.substring(0, value.indexOf('@'));
    }
    
    public Optional<Pair<String, Domain>> nameAndDomain() {
         Optional<Domain> d = domain();
         return d.map(dom -> new Pair<>(username(), dom));
    }

    public Optional<Domain> domain() {
        String[] parts = value.split("@");
        if (parts.length == 2) {
            return Domain.from(parts[1].toLowerCase());
        }
        return Optional.empty();
    }


    public NormalizedEmailAddress eliminateDomainAlias() {
        return nameAndDomain().map(pair -> {
            String name = pair.getKey();
            Domain domain = pair.getValue();
            String newDomainStr;
            if (EmailAddress.YANDEX_DOMAINS.contains(domain.lower())) {
                newDomainStr = "yandex.com";
            } else if (EmailAddress.GMAIL_DOMAINS.contains(domain.lower())) {
                newDomainStr = "gmail.com";
            } else {
                newDomainStr = domain.getValue();
            }
            return new NormalizedEmailAddress(name + "@" + newDomainStr);
        }).orElse(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NormalizedEmailAddress that = (NormalizedEmailAddress) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
