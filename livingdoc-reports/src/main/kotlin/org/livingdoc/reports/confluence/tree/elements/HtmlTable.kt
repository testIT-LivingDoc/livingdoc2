package org.livingdoc.reports.confluence.tree.elements

import com.atlassian.confluence.api.model.content.id.ContentId
import org.livingdoc.reports.html.elements.HtmlElement
import org.livingdoc.reports.html.elements.HtmlList
import org.livingdoc.reports.html.elements.HtmlTable
import org.livingdoc.reports.html.elements.determineCssClassForBackgroundColor
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
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

fun HtmlTable.cfTagRow(tag: String, documentResults: List<Pair<DocumentResult, ContentId>>) {
    appendBody {
        HtmlElement("tr") {
            child {
                HtmlElement("td") {
                    if (tag == "all")
                        child { HtmlElement("i", "all tags") }
                    else
                        text { tag }
                }
            }

            calculateSummaryNumbers(documentResults).forEach { number ->
                child {
                    HtmlElement("td", number.toString())
                }
            }
        }
    }
}

fun HtmlTable.cfReportRow(tag: String, documentResults: List<Pair<DocumentResult, ContentId>>) {
    appendBody {
        // TODO configure collapsing
        HtmlElement("tr") {
            child {
                HtmlElement("td") {
                    attr("colspan", "4")

                    child {
                        HtmlList {
                            cfLinkList(documentResults)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Calculates the summary numbers for the tag table
 *
 * @param documentResults a list of results to be examined
 * @return a list with three numbers (success, other, failed)
 *
 * TODO: this is copied from the HTML report and should be unified for both reports
 */
private fun calculateSummaryNumbers(documentResults: List<Pair<DocumentResult, ContentId>>): List<Int> {
    var numberSuccessful = 0
    var numberFailed = 0
    var numberOther = 0

    documentResults.forEach { (document, _) ->
        when (document.documentStatus) {
            is Status.Executed
            -> numberSuccessful++
            is Status.Failed
            -> numberFailed++
            is Status.Exception
            -> numberFailed++
            else
            -> numberOther++
        }
    }

    return listOf(numberSuccessful, numberOther, numberFailed)
}

private fun getReportString(value: String, cellStatus: Status): String {
    return if (cellStatus is Status.ReportActualResult) cellStatus.actualResult else value
}
