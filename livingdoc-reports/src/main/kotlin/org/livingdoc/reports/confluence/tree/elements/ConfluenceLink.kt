package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.reports.html.elements.HtmlElement

/**
 * A link element in a Confluence page tree report
 *
 * @param target The title of the page the link is pointing to
 */
class ConfluenceLink(target: String) : HtmlElement("ac:link") {
    init {
        child {
            HtmlElement("ri:page") {
                attr("ri:content-title", target)
            }
        }
    }
}
