package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.reports.html.elements.HtmlElement
import org.livingdoc.results.Status

/**
 * A link element in a Confluence page tree report
 *
 * @param target The title of the page the link is pointing to
 */
class ConfluenceLink(target: String, status: Status) : HtmlElement("ac:link") {
    init {
        child {
            HtmlElement("ri:page") {
                attr("ri:content-title", target)
            }
        }

        child {
            HtmlElement("ac:link-body") {
                child {
                    HtmlElement("span") {
                        attr("style", determineCfStylesForStatus(status))

                        text { target }
                    }
                }
            }
        }
    }
}
