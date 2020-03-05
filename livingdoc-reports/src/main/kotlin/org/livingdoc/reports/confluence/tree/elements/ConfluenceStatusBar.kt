package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.reports.html.elements.HtmlElement

class ConfluenceStatusBar(tags: List<String>) : HtmlElement("h2") {
    init {
        tags.forEach { tag ->
            child {
                ConfluenceStatus(tag)
            }
        }
    }
}
