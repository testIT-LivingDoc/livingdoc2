package org.livingdoc.reports.confluence.tree.elements

import org.jsoup.nodes.Document
import org.livingdoc.reports.html.elements.HtmlElement

class ConfluenceStatusBar : HtmlElement("h2") {
    init {
        /*
         TODO ugly hack to prevent pretty printing of confluence macros,
         which messes with coloring for the "status" macro
         */
        Document("").apply {
            outputSettings().prettyPrint(false)
        }.appendChild(element)
    }
}
