package com.example.common.markdown;

import com.vladsch.flexmark.html.AttributeProvider;
import com.vladsch.flexmark.html.IndependentAttributeProviderFactory;
import com.vladsch.flexmark.html.renderer.LinkResolverContext;
import org.jetbrains.annotations.NotNull; // Or the correct Flexmark @NotNull

public class LilaLinkAttributeProviderFactory extends IndependentAttributeProviderFactory {

    // Static factory method to match Scala object's "extends IndependentAttributeProviderFactory"
    public static LilaLinkAttributeProviderFactory create() {
        return new LilaLinkAttributeProviderFactory();
    }

    private LilaLinkAttributeProviderFactory() {}

    @NotNull // Using JetBrains annotations, ensure this is the one Flexmark uses or adjust/remove
    @Override
    public AttributeProvider apply(@NotNull LinkResolverContext context) {
        // Return a singleton instance or a new instance.
        // The original Scala code used a val (singleton) for lilaLinkAttributeProvider.
        // LilaLinkAttributeProvider itself is stateless, so a shared instance is fine.
        return LilaLinkAttributeProvider.create();
    }
}
