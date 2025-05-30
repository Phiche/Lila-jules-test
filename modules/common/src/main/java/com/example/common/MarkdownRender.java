package com.example.common;

import com.example.config.AssetDomain; // Assuming com.example.config.AssetDomain
import com.example.config.NetDomain;   // Assuming com.example.config.NetDomain
import com.example.data.Html;          // Assuming com.example.data.Html
import com.example.data.Markdown;      // Assuming com.example.data.Markdown
import com.example.common.misc.lpv.LpvEmbed; // The new placeholder
import com.example.common.log.LilaLogger; // Placeholder, or use SLF4J
// ChronometerUtils and Lap are used in the render method.
// ValueWrapper is used for Key.

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.misc.Extension; // Correct import for Flexmark Extension
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension; // For header anchors

import java.util.ArrayList;
import java.util.Collections; // For Collections.emptyList() in default PgnSourceExpand
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class MarkdownRender {

    // Key for logging/monitoring purposes
    public static final class Key extends ValueWrapper<String> { // Now extends ValueWrapper
        private Key(String value) { super(value); }
        public static Key of(String value) {
            if (value == null || value.isEmpty()) {
                // Provide a default key if null or empty to avoid issues with ValueWrapper's null check
                return new Key("default-markdown-key");
            }
            return new Key(value);
        }
    }


    // PgnSourceExpand class
    public static class PgnSourceExpand {
        private final NetDomain domain;
        private final Function<String, Optional<LpvEmbed>> getPgn; // PgnSourceId is String

        public PgnSourceExpand(NetDomain domain, Function<String, Optional<LpvEmbed>> getPgn) {
            this.domain = domain;
            this.getPgn = getPgn;
        }
        public NetDomain getDomain() { return domain; }
        public Function<String, Optional<LpvEmbed>> getGetPgn() { return getPgn; }
    }

    private final Parser parser;
    private final HtmlRenderer renderer;
    // These are stored if custom extensions need them during rendering.
    // private final Optional<PgnSourceExpand> pgnExpandOpt;
    // private final Optional<AssetDomain> assetDomainOpt;

    private final LilaLogger logger;

    // Regex for pre-processing
    // Assuming RawHtmlUtils.AT_USERNAME_REGEX_PATTERN is public static final
    private static final Pattern AT_USERNAME_REGEX_PATTERN = RawHtmlUtils.AT_USERNAME_REGEX_PATTERN;
    private static final Pattern TOO_MANY_UNDERSCORE_REGEX = Pattern.compile("(_{4,})");

    public MarkdownRender(
            boolean autoLink,
            boolean table,
            boolean strikeThrough,
            boolean header, // For headings and anchor links
            boolean blockQuote,
            boolean list,
            boolean code, // For fenced code blocks
            Optional<PgnSourceExpand> pgnExpandOpt, // Renamed for clarity
            Optional<AssetDomain> assetDomainOpt,   // Renamed for clarity
            LilaLogger logger
        ) {
        // Store PgnSourceExpand for use in PgnEmbedNodeRenderer.Factory
        final Optional<PgnSourceExpand> finalPgnExpandOpt = pgnExpandOpt;
        final Optional<AssetDomain> finalAssetDomainOpt = assetDomainOpt; // For WhitelistedImage
        this.logger = logger;

        MutableDataSet options = new MutableDataSet();
        List<Extension> extensions = new ArrayList<>();

        if (header) extensions.add(AnchorLinkExtension.create());
        if (table) {
            extensions.add(TablesExtension.create());
            // extensions.add(new TableWrapperExtension()); // Placeholder for custom extension
        }
        if (strikeThrough) extensions.add(StrikethroughExtension.create());
        if (autoLink) {
            extensions.add(AutolinkExtension.create());
            // WhitelistedImageNodeRenderer is added via NodeRendererFactory, not as a Parser Extension here.
        }

        // Placeholder for PgnEmbedExtension and LilaLinkExtension
        // These would typically be added to 'extensions' list
        // Example:
        // pgnExpandOpt.ifPresent(pgnSrc -> extensions.add(PgnEmbedExtension.create(pgnSrc)));
        // extensions.add(LilaLinkExtension.create(netDomain)); // Assuming LilaLinkExtension needs NetDomain


        options.set(Parser.EXTENSIONS, extensions)
               .set(HtmlRenderer.ESCAPE_HTML, true) // Escape HTML tags in the source
               .set(HtmlRenderer.SOFT_BREAK, "<br>\n") // Render soft line breaks as <br>
               .set(Parser.HTML_BLOCK_PARSER, false)
               .set(Parser.INDENTED_CODE_BLOCK_PARSER, false)
               .set(Parser.FENCED_CODE_BLOCK_PARSER, code);

        if (table) options.set(TablesExtension.CLASS_NAME, "slist"); // Add class to tables
        if (header) {
            options.set(AnchorLinkExtension.ANCHORLINKS_WRAP_TEXT, false);
            // Other AnchorLink options can be set here if needed
        } else {
            options.set(Parser.HEADING_PARSER, false); // Disable ATX and Setext heading parsing
        }

        if (!blockQuote) options.set(Parser.BLOCK_QUOTE_PARSER, false);
        if (!list) {
             options.set(Parser.ORDERED_LIST_PARSER, false);
             options.set(Parser.BULLET_LIST_PARSER, false);
        }


        this.parser = Parser.builder(options).build();

        HtmlRenderer.Builder rendererBuilder = HtmlRenderer.builder(options);

        // Add custom NodeRendererFactories from translated extensions here
        if (autoLink) { // autoLink is a constructor parameter of MarkdownRender
            // assetDomainOpt is also a constructor parameter of MarkdownRender
            rendererBuilder.nodeRendererFactory(new com.example.common.markdown.WhitelistedImageNodeRenderer.Factory(assetDomainOpt));
        }
        // if (table) {
        //    rendererBuilder.nodeRendererFactory(new TableWrapperNodeRenderer.Factory());
        // }

        // Add PgnEmbedNodeRenderer factory. It will use finalPgnExpandOpt.
        // If finalPgnExpandOpt is empty, its factory returns a no-op renderer.
        rendererBuilder.nodeRendererFactory(new com.example.common.markdown.PgnEmbedNodeRenderer.Factory(finalPgnExpandOpt));

        // Add TableWrapperNodeRenderer factory if table support is enabled
        if (table) { // 'table' is the boolean constructor parameter
            rendererBuilder.nodeRendererFactory(new com.example.common.markdown.TableWrapperNodeRenderer.Factory());
        }

        // Placeholder for LilaLinkNodeRenderer
        // rendererBuilder.nodeRendererFactory(new LilaLinkNodeRenderer.Factory(netDomain)); // Assuming netDomain is available or passed

        // Add the LilaLinkAttributeProviderFactory
        rendererBuilder.attributeProviderFactory(com.example.common.markdown.LilaLinkAttributeProviderFactory.create());

        this.renderer = rendererBuilder.build();
    }

    // Default constructor with common settings
    public MarkdownRender(NetDomain netDomain, AssetDomain assetDomainForImages, LilaLogger logger) {
        this(true, true, true, true, true, true, true,
             Optional.of(new PgnSourceExpand(netDomain, id -> Optional.empty())), // Default PGN expander
             Optional.of(assetDomainForImages), // This becomes assetDomainOpt in the main constructor
             logger);
    }


    private Markdown mentionsToLinks(Markdown markdown, NetDomain netDomainForMentions) {
        if (markdown == null || markdown.getValue() == null || netDomainForMentions == null) {
            return markdown != null ? markdown : new Markdown("");
        }
        // Produces Markdown links: [@username](/@/username)
        // The final HTML rendering of these links will be handled by Flexmark's LinkResolver
        // or a custom LilaLinkExtension if such paths need special handling (e.g. relative to netDomain).
        String result = AT_USERNAME_REGEX_PATTERN.matcher(markdown.getValue()).replaceAll(match -> {
            String username = match.group(1);
            // The path /@/username is a common convention for user profiles.
            // It doesn't include the domain here, as it's a Markdown link target.
            // The LinkResolver in Flexmark would make it absolute if needed.
            return String.format("[@%s](/@/%s)", username, username);
        });
        return new Markdown(result);
    }

    private Markdown preventStackOverflow(Markdown text) {
        if (text == null || text.getValue() == null) return new Markdown("");
        return new Markdown(TOO_MANY_UNDERSCORE_REGEX.matcher(text.getValue()).replaceAll("___"));
    }

    public Html render(Key key, Markdown text, NetDomain netDomainForMentions) {
        Objects.requireNonNull(key, "Render key cannot be null");
        Objects.requireNonNull(text, "Markdown text cannot be null");
        Objects.requireNonNull(netDomainForMentions, "NetDomain for mentions cannot be null");

        Supplier<String> timedRender = () -> {
            try {
                Markdown processedText = mentionsToLinks(preventStackOverflow(text), netDomainForMentions);
                return renderer.render(parser.parse(processedText.getValue()));
            } catch (StackOverflowError e) {
                if (logger != null) logger.warn(String.format("[%s] Markdown rendering StackOverflowError on input size %d",
                                                              key.getValue(), text.getValue() != null ? text.getValue().length() : 0), e);
                return text.getValue() != null ? text.getValue() : ""; // Return raw text on error
            } catch (Exception e) {
                if (logger != null) logger.warn(String.format("[%s] Markdown rendering error on input size %d",
                                                              key.getValue(), text.getValue() != null ? text.getValue().length() : 0), e);
                return text.getValue() != null ? text.getValue() : ""; // Return raw text on error
            }
        };

        Lap<String> lap = ChronometerUtils.timeSync(timedRender);

        if (logger != null) { // Check if logger is available
            lap.logIfSlow(50, logger, result -> String.format("[%s] slow markdown size:%d",
                                                             key.getValue(), text.getValue() != null ? text.getValue().length() : 0));
        }

        // Example of monitoring (assuming LilaMon and TimerPath are set up)
        // lap.mon(LilaMon.INSTANCE.markdown().time()); // This specific path needs definition in LilaMon/TimerPath

        return new Html(lap.getResult());
    }
}
