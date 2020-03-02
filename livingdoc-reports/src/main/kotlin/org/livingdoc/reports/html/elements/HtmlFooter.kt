package org.livingdoc.reports.html.elements

import org.livingdoc.results.documents.DocumentResult
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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
                    attr("href","index.html")
                }
            }
        }
    }
    child {
        HtmlElement("p") {
            text { LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " Generated with <strong>Living Doc 2</strong>." }
        }
    }
}

