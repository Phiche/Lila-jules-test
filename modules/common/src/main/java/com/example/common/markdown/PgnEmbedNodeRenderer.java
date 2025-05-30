package com.example.common.markdown;

import com.example.common.MarkdownRender.PgnSourceExpand; // Inner class from MarkdownRender
import com.example.common.misc.lpv.LpvEmbed; // Interface with PublicPgn and PrivateStudy

import com.vladsch.flexmark.ast.AutoLink;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.CoreNodeRenderer;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import java.util.Arrays;
import java.util.Collections; // For Collections.emptySet() in Factory
import java.util.HashSet;
import java.util.Objects; // For Objects.requireNonNull
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PgnEmbedNodeRenderer implements NodeRenderer {

    private final PgnSourceExpand pgnExpander;
    private final PgnRegexes pgnRegexes;

    // Helper class to hold compiled regex patterns
    private static class PgnRegexes {
        final Pattern gamePattern;
        final Pattern chapterPattern;

        PgnRegexes(String domainValue) {
            String quotedDomain = Pattern.quote(domainValue);
            // Regexes updated to better match original Scala patterns and intent
            // Game: lichess.org/{gameId}/{color}#ply or lichess.org/{gameId}#ply or lichess.org/embed/{gameId}
            // Or lichess.org/game/export/gif/{gameId}.gif (though this renderer doesn't make gifs)
            // For now, focusing on embeddable game links.
            gamePattern = Pattern.compile(
                "^(?:https?://)?" + quotedDomain + "/(?:embed/)?(?:game/)?(\\w{8})(?:/(white|black))?(?:#(\\d+))?/?$",
                Pattern.CASE_INSENSITIVE
            );
            // Study: lichess.org/study/{studyId}/{chapterId}#ply or lichess.org/study/embed/{studyId}/{chapterId}#ply
            // The original pattern for chapter was /study(?:/embed)?/(?:\w{8}/)?(\w{8})(?:#(last|\d+))?$
            // This means studyId is optional in the path if chapterId is present.
            chapterPattern = Pattern.compile(
                "^(?:https?://)?" + quotedDomain + "/study/(?:embed/)?(?:\\w{8}/)?(\\w{8})(?:#(last|\\d+))?/?$",
                Pattern.CASE_INSENSITIVE
            );
        }
    }

    public PgnEmbedNodeRenderer(DataHolder options, PgnSourceExpand pgnExpander) {
        this.pgnExpander = Objects.requireNonNull(pgnExpander, "PgnSourceExpand cannot be null");
        Objects.requireNonNull(pgnExpander.getDomain(), "PgnSourceExpand domain cannot be null");
        Objects.requireNonNull(pgnExpander.getDomain().getValue(), "PgnSourceExpand domain value cannot be null");
        this.pgnRegexes = new PgnRegexes(pgnExpander.getDomain().getValue());
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        Set<NodeRenderingHandler<?>> set = new HashSet<>();
        // Handles explicit Markdown links: [text](url)
        set.add(new NodeRenderingHandler<>(Link.class, this::renderLink));
        // Handles plain URLs that are auto-linked by AutolinkExtension
        set.add(new NodeRenderingHandler<>(AutoLink.class, this::renderAutoLink));
        return set;
    }

    private void renderLink(Link node, NodeRendererContext context, HtmlWriter html) {
        renderLinkNode(node, node.getUrl(), node.getText(), node.getTitle(), context, html);
    }

    private void renderAutoLink(AutoLink node, NodeRendererContext context, HtmlWriter html) {
        // For AutoLink, the URL is also the text. Title is usually null.
        renderLinkNode(node, node.getUrl(), node.getText(), null, context, html);
    }

    private void renderLinkNode(Node node, BasedSequence urlSequence, BasedSequence textSequence, BasedSequence titleSequence, NodeRendererContext context, HtmlWriter html) {
        if (context.isDoNotRenderLinks() || CoreNodeRenderer.isSuppressedLinkPrefix(urlSequence, context)) {
            // Render as plain text if links are suppressed or URL is unsafe (e.g. javascript:)
            // For AutoLink, textSequence is the URL. For Link, textSequence is the link text.
            html.text(textSequence.unescape());
            return;
        }

        String url = urlSequence.unescape();
        String title = (titleSequence != null && titleSequence.isNotNull()) ? titleSequence.unescape() : null;

        // Resolve link using context (handles relative paths, etc.)
        // For PGN embeds, we are interested in the raw URL provided by the user.
        // ResolvedLink resolvedLink = context.resolveLink(LinkType.LINK, url, null, title);
        // String resolvedUrl = resolvedLink.getUrl(); // This might be different from original 'url'

        // We use the raw 'url' for pattern matching PGN links.
        Matcher gameMatcher = pgnRegexes.gamePattern.matcher(url);
        if (gameMatcher.matches()) {
            String id = gameMatcher.group(1); // Game ID
            Optional<String> colorOpt = Optional.ofNullable(gameMatcher.group(2)); // white or black
            Optional<String> plyOpt = Optional.ofNullable(gameMatcher.group(3));   // ply number

            pgnExpander.getGetPgn().apply(id)
                .ifPresentOrElse(
                    embed -> renderLpvEmbed(node, textSequence, title, context, html, url, embed, colorOpt, plyOpt),
                    () -> renderPlainLink(node, textSequence, title, context, html, url) // Fallback to normal link if getPgn is empty
                );
            return;
        }

        Matcher chapterMatcher = pgnRegexes.chapterPattern.matcher(url);
        if (chapterMatcher.matches()) {
            String id = chapterMatcher.group(1); // Chapter ID
            Optional<String> plyOpt = Optional.ofNullable(chapterMatcher.group(2)); // "last" or ply number

            pgnExpander.getGetPgn().apply(id)
                .ifPresentOrElse(
                    embed -> renderLpvEmbed(node, textSequence, title, context, html, url, embed, Optional.empty(), plyOpt),
                    () -> renderPlainLink(node, textSequence, title, context, html, url)
                );
            return;
        }

        // Not a special PGN link, render as a normal link using Flexmark's resolved link
        renderPlainLink(node, textSequence, title, context, html, url);
    }

    // Renders a standard HTML link using Flexmark's ResolvedLink for safety and features
    private void renderPlainLink(Node node, BasedSequence textSequence, String title, NodeRendererContext context, HtmlWriter html, String rawUrl) {
        ResolvedLink resolvedLink = context.resolveLink(LinkType.LINK, rawUrl, null, title != null ? new MutableAttributes().addValue("title", title) : null);
        html.srcPos(node.getChars()).withAttr(resolvedLink).tag("a");
        // If Link node, render its children (the text). If AutoLink, textSequence is the URL itself.
        if (node instanceof Link) {
            context.renderChildren(node);
        } else if (node instanceof AutoLink) {
            html.text(textSequence.unescape());
        }
        html.tag("/a");
    }

    private void renderLpvEmbed(
        Node node, BasedSequence textSequence, String title, NodeRendererContext context, HtmlWriter html, String originalUrl,
        LpvEmbed embed, Optional<String> color, Optional<String> ply) {

        if (embed instanceof LpvEmbed.PublicPgn) {
            LpvEmbed.PublicPgn publicPgn = (LpvEmbed.PublicPgn) embed;
            html.attr("class", "lpv--autostart is2d"); // Default classes from lila
            html.attr("data-pgn", publicPgn.getPgn().getValue());
            color.ifPresent(c -> html.attr("data-orientation", c.toLowerCase()));
            ply.ifPresent(p -> html.attr("data-ply", p));
            if (title != null && !title.isEmpty()) {
                html.attr("title", title);
            }

            html.srcPos(node.getChars());
            html.tag("div", false); // false means not self-closing
            // For embedded PGN, the original link text or URL might be used as fallback or descriptive content
            // The Scala version doesn't seem to put any text content inside the div for PGN.
            // html.text(textSequence.unescape()); // Or originalUrl
            html.tag("/div");

        } else if (embed instanceof LpvEmbed.PrivateStudy) {
            // Render as a normal link but add a "private study" icon/indicator
            ResolvedLink resolvedLink = context.resolveLink(LinkType.LINK, originalUrl, null, title != null ? new MutableAttributes().addValue("title", title) : null);
            html.srcPos(node.getChars()).withAttr(resolvedLink).tag("a");

            html.attr("class", "private-study").attr("title", "Private study").attr("aria-label", "Private study").tag("i").tag("/i"); // Using <i> for icon
            html.text(" "); // Space after icon

            if (node instanceof Link) { // Render original link text
                context.renderChildren(node);
            } else if (node instanceof AutoLink) { // Render URL as text
                html.text(textSequence.unescape());
            }
            html.tag("/a");
        } else {
            // Unknown LpvEmbed type, fallback to rendering as a normal link
            renderPlainLink(node, textSequence, title, context, html, originalUrl);
        }
    }

    public static class Factory implements com.vladsch.flexmark.html.renderer.NodeRendererFactory {
        private final Optional<PgnSourceExpand> pgnExpanderOpt;

        public Factory(Optional<PgnSourceExpand> pgnExpanderOpt) {
            this.pgnExpanderOpt = pgnExpanderOpt;
        }

        @Override
        public NodeRenderer apply(DataHolder options) {
            return pgnExpanderOpt
                .map(expander -> (NodeRenderer) new PgnEmbedNodeRenderer(options, expander))
                .orElseGet(() -> new NodeRenderer() { // No-op or default link renderer if no expander
                    @Override
                    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
                        // This no-op renderer won't handle any nodes, so links will be rendered by CoreNodeRenderer
                        return Collections.emptySet();
                    }
                });
        }
    }
}
