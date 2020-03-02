package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.reports.html.elements.HtmlElement
import org.livingdoc.reports.html.elements.HtmlErrorContext
import org.livingdoc.reports.html.elements.HtmlTable
import org.livingdoc.reports.html.elements.determineCssClassForBackgroundColor
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.results.Status
import org.livingdoc.results.examples.decisiontables.RowResult

/**
 * Creates and adds the header for a table displaying a decision table
 *
 * @param headers The [headers][Header] of the decision table
 */
fun HtmlTable.cfHeaders(headers: List<Header>) {
    appendHead {
        HtmlElement("tr") {
            headers.forEach { (name) ->
                child {
                    HtmlElement("th", name)
                }
            }
        }
    }
}

/**
 * Creates and adds the body for a table displaying a decision table
 *
 * @param rows The [row results][RowResult] of the decision table
 */
fun HtmlTable.cfRows(rows: List<RowResult>) {

    fun appendCellToDisplayFailedRowIfNecessary(row: HtmlElement, rowStatus: Status) {
        if (rowStatus is Status.Failed || rowStatus is Status.Exception) {
            row.child {
                HtmlElement("td") {
                    cssClass(determineCfClassForStatus(rowStatus))
                    text {
                        // TODO Find better way to print the exception
                        rowStatus.toString()
                    }
                }
            }
        }
    }

    rows.forEach { (headerToField, rowResult) ->

        val newRow = HtmlElement("tr")
        headerToField.values.forEach { (value, cellStatus) ->
            newRow.child {
                HtmlElement("td") {
                    cssClass(determineCfClassForStatus(cellStatus))
                    text { getReportString(value, cellStatus) }

                    if (cellStatus is Status.Failed || cellStatus is Status.Exception) {
                        // TODO Find better way to print the exception
                        text { cellStatus.toString() }
                    }
                }
            }
        }
        appendCellToDisplayFailedRowIfNecessary(newRow, rowResult)
        appendBody { newRow }
    }
}

/**
 * Creates and adds a single row only if the entire decision table failed
 *
 * @param tableStatus The [Status] of the decision table execution
 * @param columnCount The number of columns this table has
 */
fun HtmlTable.cfRowIfTableFailed(tableStatus: Status, columnCount: Int) {
    if (tableStatus is Status.Failed || tableStatus is Status.Exception) {
        child {
            HtmlElement("tr") {
                HtmlElement("td") {
                    cssClass(determineCfClassForStatus(tableStatus))
                    child {
                        HtmlElement("td") {
                            cssClass(determineCssClassForBackgroundColor(tableStatus))
                            attr("colspan", columnCount.toString())
                            // TODO Find better way to print the exception
                            text { tableStatus.toString() }
                        }
                    }
                }
            }
        }
    }
}

private fun getReportString(value: String, cellStatus: Status): String {
    return if (cellStatus is Status.ReportActualResult) cellStatus.actualResult else value
}
