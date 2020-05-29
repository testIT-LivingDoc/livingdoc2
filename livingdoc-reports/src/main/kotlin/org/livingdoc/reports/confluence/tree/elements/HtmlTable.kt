package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.reports.html.MILLISECONDS_DIVIDER
import org.livingdoc.reports.html.elements.HtmlElement
import org.livingdoc.reports.html.elements.HtmlList
import org.livingdoc.reports.html.elements.HtmlTable
import org.livingdoc.reports.html.elements.checkFailedStatus
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.examples.decisiontables.RowResult
import java.time.Duration

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
fun HtmlTable.cfRows(headers: List<Header>, rows: List<RowResult>) {

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

        headers.forEach { header ->
            val (value, cellStatus) = headerToField[header] ?: error("Invalid header: $header")

            newRow.child {
                HtmlElement("td") {
                    cssClass(determineCfClassForStatus(cellStatus))
                    text { getReportString(value, cellStatus) }

                    when (cellStatus) {
                        is Status.Failed -> child {
                            ConfluenceError(cellStatus.reason)
                        }
                        is Status.Exception -> child {
                            ConfluenceError(cellStatus.exception)
                        }
                    }
                }
            }
        }

        appendCellToDisplayFailedRowIfNecessary(newRow, rowResult)
        appendBody { newRow }
    }
}

fun HtmlTable.cfTagRow(tag: String, documentResults: List<DocumentResult>) {
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

            calculateAndGenerateSummaryCells(documentResults).forEach { number ->
                child {
                    number
                }
            }
        }
    }
}

fun HtmlTable.cfReportRow(documentResults: List<DocumentResult>) {
    appendBody {
        HtmlElement("tr") {
            child {
                HtmlElement("td") {
                    attr("colspan", "5")

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
private fun calculateAndGenerateSummaryCells(documentResults: List<DocumentResult>): List<HtmlElement> {
    var time: Duration = Duration.ofMillis(0)
    var numberSuccessful = 0
    var numberFailed = 0
    var numberOther = 0

    documentResults.forEach { document ->
        if (checkFailedStatus(document) is Status.Failed) {
            numberFailed++
        } else {
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

        time += document.time
    }

    return generateCells(time, numberSuccessful, numberFailed, numberOther)
}

private fun generateCells(
    time: Duration,
    numberSuccessful: Int,
    numberFailed: Int,
    numberOther: Int
): List<HtmlElement> {
    return listOf(
        HtmlElement("td", "%.3fs".format(time.toMillis() / MILLISECONDS_DIVIDER)),

        HtmlElement("td") {
            text { numberSuccessful.toString() }
            if (numberFailed + numberOther == 0)
                cssClass("highlight-green")
        },
        HtmlElement("td") {
            text { numberFailed.toString() }
            if (numberFailed > 0)
                cssClass("highlight-red")
            else if (numberOther == 0)
                cssClass("highlight-green")
        },
        HtmlElement("td") {
            text { numberOther.toString() }
            if (numberOther > 0)
                cssClass("highlight-yellow")
            else if (numberFailed == 0)
                cssClass("highlight-green")
        }
    )
}

private fun getReportString(value: String, cellStatus: Status): String {
    return if (cellStatus is Status.ReportActualResult) cellStatus.actualResult else value
}
