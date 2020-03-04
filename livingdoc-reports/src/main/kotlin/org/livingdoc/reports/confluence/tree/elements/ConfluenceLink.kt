package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.reports.html.elements.HtmlElement

class ConfluenceLink(target: String) : HtmlElement("ac:link") {
    init {
        child {
            HtmlElement("ri:page") {
                attr("ri:content-title", target)
            }
        }
    }

    /**
     * A link element in a Confluence page tree report
     *
     * @param target The address the link is pointing to
     * @param block A lambda generating the content of this link
     */
    constructor(target: String, block: ConfluenceLink.() -> Unit) :
            this(target) {
        block()
    }
}
