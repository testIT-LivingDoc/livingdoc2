package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.reports.html.elements.HtmlElement
import java.io.PrintWriter
import java.io.StringWriter

/**
 * ConfluenceError displays information about an error that caused a failure in a test run.
 */
class ConfluenceError(error: Throwable) : HtmlElement("ac:structured-macro") {
    init {
        attr("ac:name", "warning")

        error.message.takeUnless { it.isNullOrBlank() }?.let { message ->
            child {
                HtmlElement("ac:parameter") {
                    attr("ac:name", "ac:title")

                    text { message }
                }
            }
        }

        child {
            HtmlElement("ac:rich-text-body") {
                child {
                    HtmlElement("pre") {
                        text {
                            StringWriter().use { writer ->
                                error.printStackTrace(PrintWriter(writer))

                                writer.toString()
                            }
                        }
                    }
                }
            }
        }
    }
}
