package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.reports.html.MILLISECONDS_DIVIDER
import org.livingdoc.reports.html.elements.HtmlElement
import org.livingdoc.results.documents.DocumentResult

/**
 * A link element in a Confluence page tree report
 *
 * @param documentResult the result of the document to link to
 */
class ConfluenceLink(documentResult: DocumentResult) : HtmlElement("ac:link") {
    init {
        child {
            HtmlElement("ri:page") {
                attr("ri:content-title", documentResult.documentClass.name)
            }
        }

        child {
            HtmlElement("ac:link-body") {
                child {
                    HtmlElement("span") {
                        attr("style", determineCfStylesForStatus(documentResult.documentStatus))

                        text {
                            documentResult.documentClass.name +
                                    " (%.3fs)".format(documentResult.time.toMillis() / MILLISECONDS_DIVIDER)
                        }
                    }
                }
            }
        }
    }
}
