package com.example.common.markdown;

import com.example.config.AssetDomain;
// import com.example.common.url.LilaUrlUtils; // Not used in this specific code

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.CoreNodeRenderer;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.TextCollectingVisitor;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.html.MutableAttributes;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class WhitelistedImageNodeRenderer implements NodeRenderer {

    private final Optional<AssetDomain> assetDomainOpt;
    private final Set<String> whitelist; // Lowercase hostnames

    public WhitelistedImageNodeRenderer(DataHolder options, Optional<AssetDomain> assetDomainOpt) {
        this.assetDomainOpt = assetDomainOpt;
        // Initialize whitelist from options or use a default
        List<String> defaultWhitelistHosts = Arrays.asList(
            "imgur.com", "i.imgur.com",
            "giphy.com", "media.giphy.com",
            "wikimedia.org", "upload.wikimedia.org",
            "creativecommons.org",
            "pexels.com", "images.pexels.com",
            "piqsels.com",
            "freeimages.com", "images.freeimages.com",
            "unsplash.com", "images.unsplash.com",
            "pixabay.com", "cdn.pixabay.com",
            "githubusercontent.com", "raw.githubusercontent.com",
            "googleusercontent.com", "lh3.googleusercontent.com",
            "i.ibb.co",
            "i.postimg.cc",
            "imgs.xkcd.com",
            "image.lichess1.org",
            "pic.lichess.org",
            "127.0.0.1"           // Localhost
        );
        this.whitelist = new HashSet<>();
        defaultWhitelistHosts.forEach(h -> this.whitelist.add(h.toLowerCase()));
        // Add assetDomain from constructor to the whitelist
        this.assetDomainOpt.ifPresent(ad -> {
            if (ad != null && ad.getValue() != null && !ad.getValue().isEmpty()) {
                this.whitelist.add(ad.getValue().toLowerCase());
            }
        });
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        Set<NodeRenderingHandler<?>> set = new HashSet<>();
        set.add(new NodeRenderingHandler<>(Image.class, this::render));
        return set;
    }

    private boolean isWhitelisted(String urlString) { // Removed unused currentAssetDomain parameter
        if (urlString == null) return false;
        URL url;
        try {
            // Prepend "http://" if no scheme is present to allow java.net.URL to parse host-only domains
            // This is a common case for user-inputted image URLs that might be just "i.imgur.com/..."
            if (!urlString.matches("^[a-zA-Z][a-zA-Z0-9+.-]*:.*")) {
                urlString = "http://" + urlString;
            }
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            return false; // Not a valid URL
        }

        String scheme = url.getProtocol();
        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            return false;
        }

        String host = url.getHost();
        if (host == null) return false;
        String lowerHost = host.toLowerCase();

        // Direct match in whitelist (e.g., "i.imgur.com")
        if (whitelist.contains(lowerHost)) return true;

        // Check subdomains: if whitelist has "github.com", allow "gist.github.com"
        for (String whitelistedHost : whitelist) {
            if (lowerHost.endsWith("." + whitelistedHost)) {
                return true;
            }
        }
        return false;
    }


    private void render(Image node, NodeRendererContext context, HtmlWriter html) {
        if (context.isDoNotRenderLinks() || CoreNodeRenderer.isSuppressedLinkPrefix(node.getUrl(), context)) {
            // If image rendering is suppressed, Flexmark's default is to render children (alt text).
            // We could choose to render nothing or just the alt text without a link.
            // For now, let's mimic the default behavior for suppressed links.
            context.renderChildren(node);
            return;
        }

        ResolvedLink resolvedLink = context.resolveLink(LinkType.IMAGE, node.getUrl().unescape(), null, null);
        String url = resolvedLink.getUrl();
        String altText = new TextCollectingVisitor().collectAndGetText(node);

        if (isWhitelisted(url)) { // Pass assetDomainOpt to isWhitelisted
            html.srcPos(node.getChars());
            html.attr("src", url);
            html.attr("alt", altText);
            // Apply title if present in resolvedLink, and any other attributes
            // html.attr(resolvedLink.getNonNullAttributes()); // This might add unwanted attributes like 'href'
            if (node.getTitle().isNotNull()) { // Use original title from node if present
                 html.attr("title", node.getTitle().unescape());
            } else if (resolvedLink.getTitle() != null) {
                 html.attr("title", resolvedLink.getTitle());
            }
            // Add class "markdown-image" for styling?
            // html.attr("class", "markdown-image");
            html.withAttr(resolvedLink); // This is generally for links, check what it does for images
            html.tagVoid("img");
        } else {
            // Render as a link if not whitelisted, as per original logic.
            // This is a common fallback for non-whitelisted/non-embeddable images.
            html.srcPos(node.getChars());
            html.attr("href", url);
            html.attr("target", "_blank"); // Open external links in new tab
            html.attr("rel", "nofollow noreferrer noopener"); // Security for external links
            if (node.getTitle().isNotNull()) {
                 html.attr("title", node.getTitle().unescape());
            } else if (resolvedLink.getTitle() != null) {
                 html.attr("title", resolvedLink.getTitle());
            }
            // html.attr(resolvedLink.getNonNullAttributes()); // Potentially problematic for A tag
            html.withAttr(resolvedLink); // Applies title if present for A tag
            html.tag("a");
            html.text(altText.isEmpty() ? url : altText); // Display URL if alt text is empty
            html.tag("/a");
        }
    }

    public static class Factory implements com.vladsch.flexmark.html.renderer.NodeRendererFactory {
        private final Optional<AssetDomain> assetDomainOpt;

        public Factory(Optional<AssetDomain> assetDomainOpt) {
            this.assetDomainOpt = assetDomainOpt;
        }

        @Override
        public NodeRenderer apply(DataHolder options) {
            // Pass the assetDomainOpt captured by the Factory's constructor
            return new WhitelistedImageNodeRenderer(options, this.assetDomainOpt);
        }
    }
}
