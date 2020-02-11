package org.livingdoc.reports.html

import org.jsoup.nodes.Element
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.results.Status
import org.livingdoc.results.examples.decisiontables.RowResult
import org.livingdoc.results.examples.scenarios.StepResult
import java.io.PrintWriter
import java.io.StringWriter

class HtmlRenderContext {
    private var popupErrorNumber = 0
    val popupErrors = ArrayList<HtmlError>()

    fun getNextErrorNumber() = ++popupErrorNumber
    fun addPopupError(htmlError: HtmlError) = popupErrors.add(htmlError)
}

class HtmlError(val number: Int, val message: String, val stacktrace: String)

interface HtmlResult

class HtmlTitle(value: String) : HtmlResult {
    private val title = Element("h2").apply { html(value) }

    override fun toString(): String {
        return title.toString()
    }
}

class HtmlLink(value: String, link: String, status: Status) : HtmlResult {
    private val title = Element("a").apply {
        setStyleClasses(determineCssClassForBackgroundColor(status))
        setLink(link)
        html(value)
    }

    override fun toString(): String {
        return title.toString()
    }
}

class HtmlDescription : HtmlResult {
    val description = Element("div")

    override fun toString(): String {
        return description.toString()
    }
}

/**
 * Adds the given paragraphs to the [HtmlDescription] element
 *
 * @param paragraphs A list of strings with each entry representing a paragraph
 */
fun HtmlDescription.paragraphs(paragraphs: List<String>) {
    paragraphs.forEach { paragraph ->
        if (paragraph.isNotEmpty())
            description.appendChild(
                Element("p").apply {
                    html(paragraph)
                }
            )
    }
}

class HtmlTable(val renderContext: HtmlRenderContext, val tableStatus: Status, val columnCount: Int) :
    HtmlResult {
    val table = Element("table")

    init {
        appendRowToDisplayFailedTableIfNecessary()
    }

    private fun appendRowToDisplayFailedTableIfNecessary() {
        if (tableStatus is Status.Failed || tableStatus is Status.Exception) {
            val tableFailedRow = Element("tr")
            tableFailedRow.appendChild(
                Element("td").apply {
                    setStyleClasses(
                        HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX,
                        determineCssClassForBackgroundColor(tableStatus)
                    )
                    attr("colspan", columnCount.toString())
                    appendChild(
                        createFailedPopupLink(
                            renderContext,
                            tableStatus
                        )
                    )
                })
            table.appendChild(tableFailedRow)
        }
    }

    override fun toString(): String {
        return table.toString()
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
    this.table.appendChild(headerRow)
}

fun HtmlTable.rows(rows: List<RowResult>) {
    val htmlTable = this

    fun appendCellToDisplayFailedRowIfNecessary(newRow: Element, rowStatus: Status) {
        if (rowStatus is Status.Failed || rowStatus is Status.Exception) {
            newRow.appendChild(
                Element("td").apply {
                    setStyleClasses(
                        HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX,
                        determineCssClassForBackgroundColor(rowStatus)
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
                    determineCssClassForBackgroundColor(cellStatus)
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
        table.appendChild(newRow)
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

class HtmlList : HtmlResult {
    val list = Element("ul")

    override fun toString(): String {
        return list.toString()
    }
}

fun HtmlList.steps(stepResults: List<StepResult>) {
    stepResults.forEach { (value, result) ->
        this.list.appendChild(
            Element("li").apply {
                setStyleClasses(determineCssClassForBackgroundColor(result))
                html(value)
            })
    }
}

private fun determineCssClassForBackgroundColor(status: Status): String {
    return when (status) {
        Status.Executed -> "background-executed"
        is Status.Disabled -> "background-disabled"
        Status.Manual -> "background-manual"
        Status.Skipped -> "background-skipped"
        Status.Unknown -> "background-unknown"
        is Status.ReportActualResult -> "background-report-result"
        is Status.Failed -> "background-failed"
        is Status.Exception -> "background-exception"
    }
}

private fun Element.setStyleClasses(vararg classes: String) {
    this.attr("class", classes.joinToString(separator = " "))
}

private fun Element.setLink(target: String) {
    this.attr("href", target)
}
