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
    block: HtmlTable.() -> Unit) :
    HtmlElement("table") {

    init {
        appendRowToDisplayFailedTableIfNecessary()
        block()
    }

    private fun appendRowToDisplayFailedTableIfNecessary() {
        if (tableStatus is Status.Failed || tableStatus is Status.Exception) {
            val tableFailedRow = HtmlElement("tr"){
                HtmlElement("td") {
                    addClass { HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX }
                    addClass { determineCssClassForBackgroundColor(tableStatus) }
                    appendChild {
                        HtmlElement("td") {
                            addClass { HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX }
                            addClass { determineCssClassForBackgroundColor(tableStatus) }
                            setAttr("colspan") { columnCount.toString() }
                            appendHtml { createFailedPopupLink(renderContext, tableStatus).toString() }
                        }
                    }
                }
            }
            appendChild { tableFailedRow }
        }
    }
}

fun HtmlTable.headers(headers: List<Header>) {
    val headerRow = Element("tr").apply {
        headers.forEach { (name) ->
            appendChild(Element("th").apply {
                setStyleClasses(HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX)
                html(name)
            })
        }
    }
    appendHtml { headerRow.toString() }
}

fun HtmlTable.rows(rows: List<RowResult>) {
    val htmlTable = this

    fun appendCellToDisplayFailedRowIfNecessary(newRow: Element, rowStatus: Status) {
        if (rowStatus is Status.Failed || rowStatus is Status.Exception) {
            newRow.appendChild(
                Element("td").apply {
                    setStyleClasses(
                        HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX,
                        determineCssClassForBackgroundColor(
                            rowStatus
                        )
                    )
                    appendChild(
                        createFailedPopupLink(
                            htmlTable.renderContext,
                            rowStatus
                        )
                    )
                })
        }
    }

    rows.forEach { (headerToField, rowResult) ->

        val newRow = Element("tr")
        headerToField.values.forEach { (value, cellStatus) ->
            newRow.appendChild(Element("td").apply {
                setStyleClasses(
                    HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX,
                    determineCssClassForBackgroundColor(
                        cellStatus
                    )
                )

                appendChild(
                    Element("span").apply {
                        setStyleClasses(HtmlReportTemplate.CSS_CLASS_RESULT_VALUE)
                        html(getReportString(value, cellStatus))
                    })

                if (cellStatus is Status.Failed || cellStatus is Status.Exception) {
                    appendChild(
                        createFailedPopupLink(
                            htmlTable.renderContext,
                            cellStatus
                        )
                    )
                }
            })
        }
        appendCellToDisplayFailedRowIfNecessary(newRow, rowResult)
        appendHtml { newRow.toString() }
    }
}

private fun getReportString(value: String, cellStatus: Status): String {
    return if (cellStatus is Status.ReportActualResult) cellStatus.actualResult else value
}

private fun createFailedPopupLink(renderContext: HtmlRenderContext, status: Status): Element {
    fun createStacktrace(e: Throwable): String {
        return StringWriter().use { stringWriter ->
            e.printStackTrace(PrintWriter(stringWriter))
            stringWriter.toString()
        }
    }

    val nextErrorNumber = renderContext.getNextErrorNumber()

    val failedPopupLink = Element("a")
    failedPopupLink.attr("href", "#popup$nextErrorNumber")

    when (status) {
        is Status.Failed -> {
            failedPopupLink.setStyleClasses(HtmlReportTemplate.CSS_CLASS_ICON_FAILED)
            renderContext.addPopupError(
                HtmlError(
                    nextErrorNumber,
                    status.reason.message ?: "",
                    createStacktrace(status.reason)
                )
            )
        }
        is Status.Exception -> {
            failedPopupLink.setStyleClasses(HtmlReportTemplate.CSS_CLASS_ICON_EXCEPTION)
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
