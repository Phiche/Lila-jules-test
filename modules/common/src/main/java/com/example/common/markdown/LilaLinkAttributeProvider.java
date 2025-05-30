package com.example.common.markdown;

import com.example.common.RawHtmlUtils; // For removeUrlTrackingParameters

import com.vladsch.flexmark.ast.AutoLink;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.html.renderer.AttributablePart;
import com.vladsch.flexmark.html.renderer.LinkResolverContext; // Not used directly here, but factory uses it
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.html.MutableAttributes;
import com.vladsch.flexmark.html.AttributeProvider;

public class LilaLinkAttributeProvider implements AttributeProvider {

    // Static factory method
    public static LilaLinkAttributeProvider create() {
        return new LilaLinkAttributeProvider();
    }

    private LilaLinkAttributeProvider() {} // Private constructor

    @Override
    public void setAttributes(Node node, AttributablePart part, MutableAttributes attributes) {
        // This provider is interested in Link and AutoLink nodes, specifically their LINK part.
        if ((node instanceof Link || node instanceof AutoLink) && part == AttributablePart.LINK) {

            String currentHref = attributes.getValue("href");
            if (currentHref != null) {
                attributes.replaceValue("href", RawHtmlUtils.removeUrlTrackingParameters(currentHref));
            }

            // Add/replace rel and target for all standard links processed by this provider.
            // These attributes are generally for external links or links where such behavior is desired.
            // If a link is internal (e.g., to the same site), target="_blank" might not always be wanted.
            // However, the original Scala LilaLinkExtension applied these broadly.
            // PgnEmbedNodeRenderer might handle its own specific link attributes if it renders an <a> tag.
            // This provider will affect links rendered by core Flexmark renderers or other custom renderers
            // that don't set these attributes themselves.

            attributes.replaceValue("rel", "nofollow noreferrer noopener"); // Added noopener for security
            attributes.replaceValue("target", "_blank");
        }
    }
}
