package org.livingdoc.reports.html.elements

import org.jsoup.nodes.Element
import org.livingdoc.results.Status

class HtmlLink(linkAddress: String, status: Status) :
    HtmlElement("a") {

    init {
        element.apply {
            setStyleClasses(
                determineCssClassForBackgroundColor(
                    status
                )
            )
            attr("href", linkAddress)
        }
    }

    constructor(linkAddress: String, status: Status, value: String) :
            this(linkAddress, status) {
        appendHtml { value }
    }

    constructor(linkAddress: String, status: Status, block: HtmlLink.() -> Unit) :
            this(linkAddress, status) {
        block()
    }
}
