
package org.livingdoc.reports.html

import org.livingdoc.reports.html.elements.HtmlElement
import org.livingdoc.reports.html.elements.HtmlErrorContext

class HtmlReportTemplate {

    companion object {
        const val CSS_CLASS_BORDER_BLACK_ONEPX = "border-black-onepx"
        const val CSS_CLASS_ICON_FAILED = "icon-failed"
        const val CSS_CLASS_ICON_EXCEPTION = "icon-exception"
        const val CSS_CLASS_RESULT_VALUE = "result-value"
        const val HTML_HEAD_STYLE_CONTENT = "<link rel=\"stylesheet\" href=\"./style.css\">"
        const val HTML_HEAD_SCRIPT_CONTENT = "<script src=\"./script.js\"></script>"
    }

    fun renderElementListTemplate(
        htmlResults: List<HtmlElement>,
        errorContext: HtmlErrorContext
    ): String {
        return """
            <!DOCTYPE html>
            <html>
                <head>
                    <meta charset="UTF-8">
                    ${HTML_HEAD_STYLE_CONTENT.trimIndent()}
                    ${HTML_HEAD_SCRIPT_CONTENT.trimIndent()}
                </head>
                <body>
                    ${htmlResults.joinToString(separator = "\n")}
                    ${createErrorPopups(errorContext)}
                </body>
            </html>
            """.trimIndent()
    }

    private fun createErrorPopups(errorContext: HtmlErrorContext): String {
        return errorContext.popupErrors.joinToString(separator = "") { error ->
            """

            <div id="popup${error.number}" class="overlay">
                <div class="popup">
                    <h2>${error.message}</h2>
                    <a class="close" href="#">&times;</a>
                    <div class="content">
                        <pre>
                            ${error.stacktrace}
                        </pre>
                    </div>
                </div>
            </div>
            """.trimIndent()
        }
    }
}
