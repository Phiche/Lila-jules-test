package com.example.config;

import com.example.email.EmailAddress;
import java.util.List;

public class NetConfig {
    private final NetDomain domain;
    private final NetDomain prodDomain;
    private final BaseUrl baseUrl;
    private final AssetDomain assetDomain;
    private final AssetBaseUrl assetBaseUrl;
    private final AssetBaseUrlInternal assetBaseUrlInternal;
    private final boolean stageBanner;
    private final String siteName;
    private final List<String> socketDomains;
    private final List<String> socketAlts;
    private final boolean crawlable;
    private final RateLimit rateLimit;
    private final EmailAddress email;
    private final boolean logRequests;

    public NetConfig(NetDomain domain, NetDomain prodDomain, BaseUrl baseUrl,
                     AssetDomain assetDomain, AssetBaseUrl assetBaseUrl,
                     AssetBaseUrlInternal assetBaseUrlInternal, boolean stageBanner,
                     String siteName, List<String> socketDomains, List<String> socketAlts,
                     boolean crawlable, RateLimit rateLimit, EmailAddress email, boolean logRequests) {
        this.domain = domain;
        this.prodDomain = prodDomain;
        this.baseUrl = baseUrl;
        this.assetDomain = assetDomain;
        this.assetBaseUrl = assetBaseUrl;
        this.assetBaseUrlInternal = assetBaseUrlInternal;
        this.stageBanner = stageBanner;
        this.siteName = siteName;
        this.socketDomains = socketDomains;
        this.socketAlts = socketAlts;
        this.crawlable = crawlable;
        this.rateLimit = rateLimit;
        this.email = email;
        this.logRequests = logRequests;
    }

    // Add getters for all fields
    public NetDomain getDomain() { return domain; }
    public NetDomain getProdDomain() { return prodDomain; }
    public BaseUrl getBaseUrl() { return baseUrl; }
    public AssetDomain getAssetDomain() { return assetDomain; }
    public AssetBaseUrl getAssetBaseUrl() { return assetBaseUrl; }
    public AssetBaseUrlInternal getAssetBaseUrlInternal() { return assetBaseUrlInternal; }
    public boolean isStageBanner() { return stageBanner; }
    public String getSiteName() { return siteName; }
    public List<String> getSocketDomains() { return socketDomains; }
    public List<String> getSocketAlts() { return socketAlts; }
    public boolean isCrawlable() { return crawlable; }
    public RateLimit getRateLimit() { return rateLimit; }
    public EmailAddress getEmail() { return email; }
    public boolean isLogRequests() { return logRequests; }
}
