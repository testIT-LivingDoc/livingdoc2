package org.livingdoc.engine.reporting

import org.jsoup.nodes.Element
import org.livingdoc.engine.execution.Result
import org.livingdoc.engine.execution.examples.decisiontables.model.RowResult
import org.livingdoc.engine.execution.examples.scenarios.model.StepResult
import org.livingdoc.repositories.model.decisiontable.Header
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

class HtmlTable(val renderContext: HtmlRenderContext, val tableResult: Result, val columnCount: Int) : HtmlResult {
    val table = Element("table")

    init {
        appendRowToDisplayFailedTableIfNecessary()
    }

    private fun appendRowToDisplayFailedTableIfNecessary() {
        if (tableResult is Result.Failed || tableResult is Result.Exception) {
            val tableFailedRow = Element("tr")
            tableFailedRow.appendChild(
                Element("td").apply {
                    setStyleClasses(
                        HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX,
                        determineCssClassForBackgroundColor(tableResult)
                    )
                    attr("colspan", columnCount.toString())
                    appendChild(createFailedPopupLink(renderContext, tableResult))
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

    fun appendCellToDisplayFailedRowIfNecessary(newRow: Element, rowResult: Result) {
        if (rowResult is Result.Failed || rowResult is Result.Exception) {
            newRow.appendChild(
                Element("td").apply {
                    setStyleClasses(
                        HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX,
                        determineCssClassForBackgroundColor(rowResult)
                    )
                    appendChild(createFailedPopupLink(htmlTable.renderContext, rowResult))
                })
        }
    }

    rows.forEach { (headerToField, rowResult) ->

        val newRow = Element("tr")
        headerToField.values.forEach { (value, cellResult) ->
            newRow.appendChild(
                Element("td").apply {
                    setStyleClasses(
                        HtmlReportTemplate.CSS_CLASS_BORDER_BLACK_ONEPX,
                        determineCssClassForBackgroundColor(cellResult)
                    )

                    appendChild(
                        Element("span").apply {
                            setStyleClasses(HtmlReportTemplate.CSS_CLASS_RESULT_VALUE)
                            html(value)
                        })

                    if (cellResult is Result.Failed || cellResult is Result.Exception) {
                        appendChild(createFailedPopupLink(htmlTable.renderContext, cellResult))
                    }
                })
        }
        appendCellToDisplayFailedRowIfNecessary(newRow, rowResult)
        table.appendChild(newRow)
    }
}

private fun createFailedPopupLink(renderContext: HtmlRenderContext, result: Result): Element {
    fun createStacktrace(e: Throwable): String {
        return StringWriter().use { stringWriter ->
            e.printStackTrace(PrintWriter(stringWriter))
            stringWriter.toString()
        }
    }

    val nextErrorNumber = renderContext.getNextErrorNumber()

    val failedPopupLink = Element("a")
    failedPopupLink.attr("href", "#popup$nextErrorNumber")

    when (result) {
        is Result.Failed -> {
            failedPopupLink.setStyleClasses(HtmlReportTemplate.CSS_CLASS_ICON_FAILED)
            renderContext.addPopupError(
                HtmlError(nextErrorNumber, result.reason.message ?: "", createStacktrace(result.reason))
            )
        }
        is Result.Exception -> {
            failedPopupLink.setStyleClasses(HtmlReportTemplate.CSS_CLASS_ICON_EXCEPTION)
            renderContext.addPopupError(
                HtmlError(nextErrorNumber, result.exception.message ?: "", createStacktrace(result.exception))
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

private fun determineCssClassForBackgroundColor(result: Result): String {
    return when (result) {
        Result.Executed -> "background-executed"
        Result.Skipped -> "background-skipped"
        Result.Unknown -> "background-unknown"
        is Result.Failed -> "background-failed"
        is Result.Exception -> "background-exception"
    }
}

private fun Element.setStyleClasses(vararg classes: String) {
    this.attr("class", classes.joinToString(separator = " "))
}
