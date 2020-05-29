package org.livingdoc.reports.html.elements

import org.livingdoc.reports.html.HtmlReportTemplate
import org.livingdoc.reports.html.MILLISECONDS_DIVIDER
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.examples.decisiontables.RowResult
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.Path
import java.time.Duration

/**
 * A table in a HTML report
 */
class HtmlTable(
    block: HtmlTable.() -> Unit
) :
    HtmlElement("table") {

    private val head = HtmlElement("thead")
    private val body = HtmlElement("tbody")

    init {
        child { head }
        child { body }
        block()
    }

    /**
     * Appends a row to the head of this table
     */
    fun appendHead(block: HtmlTable.() -> HtmlElement) {
        head.child { block() }
    }

    /**
     * Appends a row to the body of this table
     */
    fun appendBody(block: HtmlTable.() -> HtmlElement) {
        body.child { block() }
    }
}

/**
 * Creates and adds the header for a table displaying a decision table
 *
 * @param headers The [headers][Header] of the decision table
 */
fun HtmlTable.headers(headers: List<Header>) {
    appendHead {
        HtmlElement("tr") {
            headers.forEach { (name) ->
                child {
                    HtmlElement("th") {
                        cssClass(HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX)
                        text { name }
                    }
                }
            }
        }
    }
}

/**
 * Creates and adds the body for a table displaying a decision table
 *
 * @param errorContext A [HtmlErrorContext] containing popups for error stack traces
 * @param rows The [row results][RowResult] of the decision table
 */
fun HtmlTable.rows(errorContext: HtmlErrorContext, headers: List<Header>, rows: List<RowResult>) {

    fun appendCellToDisplayFailedRowIfNecessary(row: HtmlElement, rowStatus: Status) {
        if (rowStatus is Status.Failed || rowStatus is Status.Exception) {
            row.child {
                HtmlElement("td") {
                    cssClass(HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX)
                    cssClass(determineCssClassForBackgroundColor(rowStatus))

                    child {
                        createFailedPopupLink(
                            errorContext,
                            rowStatus
                        )
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
                    cssClass(HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX)
                    cssClass(determineCssClassForBackgroundColor(cellStatus))

                    child {
                        HtmlElement("span") {
                            cssClass(HtmlReportTemplate.CSS_CLASS_RESULT_VALUE)
                            text { getReportString(value, cellStatus) }
                        }
                    }
                    if (cellStatus is Status.Failed || cellStatus is Status.Exception) {
                        child {
                            createFailedPopupLink(
                                errorContext,
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

/**
 * Creates and adds a single row only if the entire decision table failed
 *
 * @param errorContext A [HtmlErrorContext] containing popups for error stack traces
 * @param tableStatus The [Status] of the decision table execution
 * @param columnCount The number of columns this table has
 */
fun HtmlTable.rowIfTableFailed(errorContext: HtmlErrorContext, tableStatus: Status, columnCount: Int) {
    if (tableStatus is Status.Failed || tableStatus is Status.Exception) {
        child {
            HtmlElement("tr") {
                HtmlElement("td") {
                    cssClass(HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX)
                    cssClass(determineCssClassForBackgroundColor(tableStatus))
                    child {
                        HtmlElement("td") {
                            cssClass(HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX)
                            cssClass(determineCssClassForBackgroundColor(tableStatus))
                            attr("colspan", columnCount.toString())
                            text { createFailedPopupLink(errorContext, tableStatus).toString() }
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

private fun createFailedPopupLink(errorContext: HtmlErrorContext, status: Status): HtmlElement {
    fun createStacktrace(e: Throwable): String {
        return StringWriter().use { stringWriter ->
            e.printStackTrace(PrintWriter(stringWriter))
            stringWriter.toString()
        }
    }

    val nextErrorNumber = errorContext.getNextErrorNumber()

    val failedPopupLink = HtmlLink("#popup$nextErrorNumber")

    when (status) {
        is Status.Failed -> {
            failedPopupLink.cssClass(HtmlReportTemplate.CSS_CLASS_ICON_FAILED)
            errorContext.addPopupError(
                HtmlError(
                    nextErrorNumber,
                    status.reason.message ?: "",
                    createStacktrace(status.reason)
                )
            )
        }
        is Status.Exception -> {
            failedPopupLink.cssClass(HtmlReportTemplate.CSS_CLASS_ICON_EXCEPTION)
            errorContext.addPopupError(
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

/**
 *
 */
fun HtmlTable.summaryTableHeader() {
    appendHead {
        HtmlElement("tr") {
            child { HtmlElement("th", "Tag") }
            child { HtmlElement("th", "Time") }
            child { HtmlElement("th", "✔") }
            child { HtmlElement("th", "✖") }
            child { HtmlElement("th", "···") }
        }
    }
}

/**
 * Creates and adds a row that displays a tag
 *
 * @param tag The tag that is displayed by this row
 * If the tag is "all" the row will be highlighted
 * @param documentResults A list of [document results][DocumentResult] mapped to the path of their HTML report
 */
fun HtmlTable.tagRow(tag: String, documentResults: List<Pair<DocumentResult, Path>>) {
    appendBody {
        HtmlElement("tr") {
            child {
                HtmlElement("td") {
                    child {
                        HtmlElement("span") {
                            cssClass("indicator")
                            attr("id", "indicator_$tag")
                            attr("onClick", "collapse('indicator_$tag', 'ID_$tag')")
                            text { "⏵" }
                        }
                    }
                    if (tag == "all")
                        child { HtmlElement("i", "all tags") }
                    else
                        text { tag }
                }
            }

            calculateAndGenerateSummaryCells(documentResults).forEach {
                child {
                    it
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
 */
private fun calculateAndGenerateSummaryCells(documentResults: List<Pair<DocumentResult, Path>>): List<HtmlElement> {
    var time: Duration = Duration.ofMillis(0)
    var numberSuccessful = 0
    var numberFailed = 0
    var numberOther = 0

    documentResults.forEach { (document, _) ->
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
        HtmlElement("td") {
            cssClass("timeCell")
            text { "%.3f".format(time.toMillis() / MILLISECONDS_DIVIDER) + "s" }
        },
        HtmlElement("td") {
            text { numberSuccessful.toString() }
            if (numberFailed + numberOther == 0)
                cssClass("successfulCell")
        },
        HtmlElement("td") {
            text { numberFailed.toString() }
            if (numberFailed > 0)
                cssClass("failedCell")
            else if (numberOther == 0)
                cssClass("successfulCell")
        },
        HtmlElement("td") {
            text { numberOther.toString() }
            if (numberOther > 0)
                cssClass("otherCell")
            else if (numberFailed == 0)
                cssClass("successfulCell")
        }
    )
}

/**
 * Creates and adds a collapsible row with a list of document result links
 */
fun HtmlTable.collapseRow(tag: String, documentResults: List<Pair<DocumentResult, Path>>) {
    appendBody {
        HtmlElement("tr") {
            attr("id", "ID_$tag")
            cssClass("hidden")
            child {
                HtmlElement("td") {
                    attr("colspan", "5")
                    child {
                        HtmlList {
                            linkList(documentResults)
                        }
                    }
                }
            }
        }
    }
}
