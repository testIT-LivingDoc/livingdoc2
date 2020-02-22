package org.livingdoc.reports.html.elements

import org.livingdoc.results.Status

class HtmlLink(linkAddress: String, status: Status) :
    HtmlElement("a") {

    init {
        addClass(determineCssClassForBackgroundColor(status))
        setAttr("href", linkAddress)
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
