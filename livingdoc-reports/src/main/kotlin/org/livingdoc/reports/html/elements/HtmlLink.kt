package org.livingdoc.reports.html.elements

import org.livingdoc.results.Status

/**
 * A link element in a HTML report
 *
 * @param linkAddress The address the link is pointing to
 */
class HtmlLink(linkAddress: String) :
    HtmlElement("a") {

    init {
        attr("href", linkAddress)
    }

    /**
     * A link element in a HTML report
     *
     * @param linkAddress The address the link is pointing to
     * @param value The text displayed by the link
     */
    constructor(linkAddress: String, value: String) :
            this(linkAddress) {
        text { value }
    }

    /**
     * A link element in a HTML report
     *
     * @param linkAddress The address the link is pointing to
     * @param block A lambda generating the content of this link
     */
    constructor(linkAddress: String, block: HtmlLink.() -> Unit) :
            this(linkAddress) {
        block()
    }
}

/**
 * Generates a link that points to a test execution
 */
fun HtmlLink.resultLink(className: String, status: Status) {
    cssClass(determineCssClassForBackgroundColor(status))
    text { className }
}
