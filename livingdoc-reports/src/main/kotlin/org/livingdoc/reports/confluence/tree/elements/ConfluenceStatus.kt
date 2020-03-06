package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.reports.html.elements.HtmlElement

/**
 * A ConfluenceStatus represents a single tag in a [ConfluenceStatusBar]
 */
class ConfluenceStatus(tag: String) : HtmlElement("ac:structured-macro") {
    init {
        attr("ac:name", "status")
        attr("ac:schema-version", "1")

        child {
            HtmlElement("ac:parameter") {
                attr("ac:name", "colour")

                text { "Blue" }
            }
        }

        child {
            HtmlElement("ac:parameter") {
                attr("ac:name", "title")

                text { tag }
            }
        }
    }
}
