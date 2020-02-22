package org.livingdoc.reports.html.elements

import org.jsoup.nodes.Element
import org.livingdoc.reports.html.HtmlReportTemplate
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.results.Status
import org.livingdoc.results.examples.decisiontables.RowResult
import java.io.PrintWriter
import java.io.StringWriter

class HtmlTable(
    val renderContext: HtmlRenderContext,
    val tableStatus: Status,
    val columnCount: Int,
    block: HtmlTable.() -> Unit
) :
    HtmlElement("table") {

    private val head = HtmlElement("thead")
    private val body = HtmlElement("tbody")

    init {
        appendChild { head }
        appendChild { body }
        appendRowToDisplayFailedTableIfNecessary()
        block()
    }

    private fun appendRowToDisplayFailedTableIfNecessary() {
        if (tableStatus is Status.Failed || tableStatus is Status.Exception) {
            val tableFailedRow = HtmlElement("tr") {
                HtmlElement("td") {
                    addClass(HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX)
                    addClass(determineCssClassForBackgroundColor(tableStatus))
                    appendChild {
                        HtmlElement("td") {
                            addClass(HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX)
                            addClass(determineCssClassForBackgroundColor(tableStatus))
                            setAttr("colspan", columnCount.toString())
                            appendHtml { createFailedPopupLink(renderContext, tableStatus).toString() }
                        }
                    }
                }
            }
            appendChild { tableFailedRow }
        }
    }

    fun appendHead(block: HtmlTable.() -> HtmlElement) {
        head.appendChild { block() }
    }

    fun appendBody(block: HtmlTable.() -> HtmlElement) {
        body.appendChild { block() }
    }
}

fun HtmlTable.headers(headers: List<Header>) {
    appendHead {
        HtmlElement("tr") {
            headers.forEach { (name) ->
                appendChild {
                    HtmlElement("th") {
                        addClass(HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX)
                        appendHtml { name }
                    }
                }
            }
        }
    }
}

fun HtmlTable.rows(rows: List<RowResult>) {
    val htmlTable = this

    fun appendCellToDisplayFailedRowIfNecessary(row: HtmlElement, rowStatus: Status) {
        if (rowStatus is Status.Failed || rowStatus is Status.Exception) {
            row.appendChild {
                HtmlElement("td") {
                    addClass(HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX)
                    addClass(determineCssClassForBackgroundColor(rowStatus))

                    appendChild {
                        createFailedPopupLink(
                            htmlTable.renderContext,
                            rowStatus
                        )
                    }
                }
            }
        }
    }

    rows.forEach { (headerToField, rowResult) ->

        val newRow = HtmlElement("tr")
        headerToField.values.forEach { (value, cellStatus) ->
            newRow.appendChild {
                HtmlElement("td") {
                    addClass(HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX)
                    addClass(determineCssClassForBackgroundColor(cellStatus))

                    appendChild {
                        HtmlElement("span") {
                            addClass(HtmlReportTemplate.CSS_CLASS_RESULT_VALUE)
                            appendHtml { getReportString(value, cellStatus) }
                        }
                    }
                    if (cellStatus is Status.Failed || cellStatus is Status.Exception) {
                        appendChild {
                            createFailedPopupLink(
                                htmlTable.renderContext,
                                cellStatus
                            )
                        }
                    }
                }
            }
        }
        appendCellToDisplayFailedRowIfNecessary(newRow, rowResult)
        appendBody { newRow }
    }
}

private fun getReportString(value: String, cellStatus: Status): String {
    return if (cellStatus is Status.ReportActualResult) cellStatus.actualResult else value
}

private fun createFailedPopupLink(renderContext: HtmlRenderContext, status: Status): HtmlElement {
    fun createStacktrace(e: Throwable): String {
        return StringWriter().use { stringWriter ->
            e.printStackTrace(PrintWriter(stringWriter))
            stringWriter.toString()
        }
    }

    val nextErrorNumber = renderContext.getNextErrorNumber()

    val failedPopupLink = HtmlLink("#popup$nextErrorNumber", status)

    when (status) {
        is Status.Failed -> {
            failedPopupLink.addClass(HtmlReportTemplate.CSS_CLASS_ICON_FAILED)
            renderContext.addPopupError(
                HtmlError(
                    nextErrorNumber,
                    status.reason.message ?: "",
                    createStacktrace(status.reason)
                )
            )
        }
        is Status.Exception -> {
            failedPopupLink.addClass(HtmlReportTemplate.CSS_CLASS_ICON_EXCEPTION)
            renderContext.addPopupError(
                HtmlError(
                    nextErrorNumber,
                    status.exception.message ?: "",
                    createStacktrace(status.exception)
                )
            )
        }
    }
    return failedPopupLink
}
