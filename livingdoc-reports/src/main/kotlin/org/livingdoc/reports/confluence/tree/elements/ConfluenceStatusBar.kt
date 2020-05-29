package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.reports.html.elements.HtmlElement
import org.livingdoc.results.documents.DocumentResult

/**
 * A ConfluenceStatusBar lists all tags of a [DocumentResult] in a [ConfluenceReport]
 */
class ConfluenceStatusBar(tags: List<String>) : HtmlElement("h2") {
    init {
        tags.forEach { tag ->
            child {
                ConfluenceStatus(tag)
            }
        }
    }
}
