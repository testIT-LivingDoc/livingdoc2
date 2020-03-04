package org.livingdoc.reports.html.elements

class HtmlFooter(content: HtmlFooter.() -> Unit) : HtmlElement("div") {

    init {
        cssClass("footer")
        content()
    }
}

/**
 * This creates the footer for the reports
 */
fun HtmlFooter.populateFooter() {
    child {
        HtmlElement("p") {
            child {
                HtmlElement("a") {
                    text {
                        "↩ Index"
                    }
                    attr("href", "index.html")
                }
            }
        }
    }
    child {
        HtmlElement("p") {
            text { "Generated with <strong>Living Doc 2</strong>." }
        }
    }
}
