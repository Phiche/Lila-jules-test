package com.example.common.markdown;

import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;

import java.util.HashSet;
import java.util.Set;

public class TableWrapperNodeRenderer implements NodeRenderer {

    public TableWrapperNodeRenderer(DataHolder options) {
        // Constructor can take options if needed, but this specific renderer doesn't use them directly.
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        Set<NodeRenderingHandler<?>> set = new HashSet<>();
        set.add(new NodeRenderingHandler<>(TableBlock.class, this::render));
        return set;
    }

    private void render(TableBlock node, NodeRendererContext context, HtmlWriter html) {
        // This renderer wraps the standard TableBlock rendering with a div.
        // The standard TableBlock rendering (which includes adding class="slist" to the <table> tag
        // if TablesExtension.CLASS_NAME is set) will be done by context.renderChildren(node)
        // because TableBlock itself is a node that has children (TableHead, TableBody, TableRow, etc.).
        // Flexmark's CoreNodeRenderer for TableBlock will handle the actual <table> rendering.
        // This custom renderer just adds the wrapper div.

        html.attr("class", "slist-wrapper").withAttr().tag("div"); // open <div class="slist-wrapper">
        context.renderChildren(node); // This will delegate to other renderers for children of TableBlock,
                                      // including the default TableBlock renderer if no other custom one overrides it
                                      // for TableBlock itself (which is not the case here, this IS the renderer for TableBlock).
                                      // More accurately, for a node like TableBlock, renderChildren usually renders its content (head, body).
                                      // The TableBlock itself is handled by this 'render' method.
                                      // However, the standard way to ensure default rendering of the node itself within a wrapper
                                      // is tricky. Most wrappers either fully take over or wrap children.
                                      // Let's assume the goal is to wrap the standard HTML table output.
                                      // The original Scala `context.delegateRender()` suggests letting the default take over.
                                      // Flexmark's `NodeRendererContext.render(Node)` can be used to render a specific node
                                      // using the registered renderers.
                                      // To achieve a wrapper, we'd do:
                                      // 1. Render opening div tag
                                      // 2. Create a new HtmlWriter or capture output of default table rendering
                                      // 3. Render closing div tag
                                      // A simpler approach if this renderer *is* the primary renderer for TableBlock:
                                      // html.attr("class", "slist-wrapper").tag("div");
                                      //    html.tag("table"); // Start the table, apply attributes if needed
                                      //    context.renderChildren(node); // Render caption, thead, tbody, tfoot
                                      //    html.tag("/table");
                                      // html.tag("/div");
                                      // This would bypass the default TableNodeRenderer's logic for adding "slist" to <table>.

                                      // The most common pattern for "wrapping" is that this renderer IS the renderer for TableBlock.
                                      // It should render the <table> tag and its contents.
                                      // The default TableNodeRenderer from TablesExtension adds the class to the <table>.
                                      // If this custom renderer is registered, it REPLACES the default one for TableBlock.
                                      // So, this renderer must now also handle rendering the <table> tag itself.

                                      // Correct approach: Render the wrapper, then delegate to the *next* renderer
                                      // for TableBlock, or manually render the table.
                                      // Flexmark doesn't have a simple "delegateRender" for the same node type.
                                      // The most straightforward for a wrapper is:
                                      // html.raw("<div class=\"slist-wrapper\">");
                                      // context.renderChildren(node); // Renders <thead>, <tbody> - NOT the <table> tag itself.
                                      // This means this renderer needs to output the <table> tag.

                                      // Let's use the approach from the prompt, assuming renderChildren
                                      // correctly causes the inner table to be rendered by its default/configured renderer.
                                      // This usually works if this renderer is for a *parent* type not TableBlock itself,
                                      // or if TableBlock's children are what constitute the table content.
                                      // Flexmark's TableBlock has children like TableHead, TableBody.
                                      // The default renderer for TableBlock handles <table>, then calls renderChildren.
                                      // If this replaces it, we must render <table>.

                                      // The simplest way to achieve the wrap and keep default table rendering:
                                      // is often not to replace the TableBlock renderer but to have a parent
                                      // that this renderer handles, or use CSS for styling.
                                      // However, if we must wrap, let's render the table explicitly.

        // Render the table with its default class ("slist" if configured)
        // This custom renderer now takes full responsibility for TableBlock rendering.
        html.srcPos(node.getChars()).withAttr().tag("table");
        // Apply class "slist" if configured in options (MarkdownRender does this)
        String tableClassName = context.getHtmlOptions().tableClassName; // Accessing configured class
        if (tableClassName != null && !tableClassName.isEmpty()) {
            html.attr("class", tableClassName);
        }
        context.renderChildren(node); // Renders THEAD, TBODY, etc.
        html.tag("/table");
        html.tag("/div"); // close </div>
    }

    public static class Factory implements com.vladsch.flexmark.html.renderer.NodeRendererFactory {
        @Override
        public NodeRenderer apply(DataHolder options) {
            return new TableWrapperNodeRenderer(options);
        }
    }
}
