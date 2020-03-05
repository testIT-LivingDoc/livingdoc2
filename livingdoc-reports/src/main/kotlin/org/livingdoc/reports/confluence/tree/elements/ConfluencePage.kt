package org.livingdoc.reports.confluence.tree.elements

import org.jsoup.nodes.Document
import org.livingdoc.reports.html.elements.HtmlElement

open class ConfluencePage : HtmlElement("div") {
    init {
        Document("").apply {
            outputSettings().prettyPrint(false)
        }.appendChild(element)
    }

    override fun toString(): String {
        return element.html()
    }
}
