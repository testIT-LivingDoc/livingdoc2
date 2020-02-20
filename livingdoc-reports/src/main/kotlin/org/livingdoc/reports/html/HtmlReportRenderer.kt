package org.livingdoc.reports.html

import org.jsoup.nodes.Element
import org.livingdoc.config.YamlUtils
import org.livingdoc.reports.ReportWriter
import org.livingdoc.reports.spi.Format
import org.livingdoc.reports.spi.ReportRenderer
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import org.livingdoc.results.examples.scenarios.ScenarioResult
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Format("html")
class HtmlReportRenderer : ReportRenderer {

    private val renderContext = HtmlRenderContext()

    override fun render(documentResults: List<DocumentResult>, config: Map<String, Any>) {
        val htmlConfig = YamlUtils.toObject(config, HtmlReportConfig::class)
        val outputFolder = Paths.get(
            htmlConfig.outputDir,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
        ).toString()
        val reportWriter = ReportWriter(outputFolder, fileExtension = "html")

        val generatedReports = documentResults.map { documentResult ->
            val html = render(documentResult)
            documentResult to reportWriter.writeToFile(
                html,
                documentResult.documentClass.name
            )
        }

        if (htmlConfig.generateIndex) {
            reportWriter.writeToFile(
                renderIndex(generatedReports),
                "index"
            )
        }
    }

    /**
     * Create a html string from a [DocumentResult]
     */
    fun render(documentResult: DocumentResult): String {
        val exampleResult = documentResult.results

        val htmlResults = exampleResult.flatMap { result ->
            when (result) {
                is DecisionTableResult -> handleDecisionTableResult(result)
                is ScenarioResult -> handleScenarioResult(result)
                else -> throw IllegalArgumentException("Unknown Result type.")
            }
        }.filterNotNull()

        return HtmlReportTemplate()
            .renderResultListTemplate(htmlResults, renderContext)
    }

    /**
     * This renders the two column layout for the index/summary page
     */
    private fun renderIndex(reports: List<Pair<DocumentResult, Path>>): String {

        val columnContainer = Element("div")
            .addClass("flex")
            .appendChild(Element("script").html(
                """function collapse (indicator, row) {
                    var indicatorElem = document.getElementById(indicator);
                    var rowElem = document.getElementById(row);
                    if (rowElem.classList.contains("hidden")) {
                        indicatorElem.innerHTML = "⏷";
                    } else {
                        indicatorElem.innerHTML = "⏵";
                    }
                    rowElem.classList.toggle("hidden");
                }"""
            ))
            .appendChild(renderIndexList(reports))
            .appendChild(renderTagList(reports))

        return HtmlReportTemplate()
            .renderElementTemplate(columnContainer, renderContext)
    }

    /**
     * This returns the left column for the index/summary page with a title and a list of all documents
     */
    private fun renderIndexList(reports: List<Pair<DocumentResult, Path>>): Element {
        val indexListDiv = Element("div").addClass("flex-50")

        indexListDiv.appendChild(Element("h2").html("Index"))

        indexListDiv.appendChild(renderLinkList(reports))
        return indexListDiv
    }

    /**
     *  This returns the right column with the tag table
     */
    private fun renderTagList(reports: List<Pair<DocumentResult, Path>>): Element {
        val tagListDiv = Element("div").addClass("flex-50")

        tagListDiv.appendChild(Element("h2").html("Tag Summary"))

        val reportsByTag = reports.flatMap { report ->
            listOf(
                listOf("all" to report),
                report.first.tags.map { tag ->
                    tag to report
                }
            ).flatten()
        }.groupBy({ it.first }, { it.second })

        val tagTable = Element("table").attr("id", "summary-table")
        tagTable.appendChild(summaryTableHeader())

        reportsByTag.map { (tag, documentResults) ->
            tagTable.appendChild(tagRow(tag, documentResults))
            tagTable.appendChild(collapseRow(tag, documentResults))
        }

        tagListDiv.appendChild(tagTable)

        return tagListDiv
    }

    private fun handleDecisionTableResult(decisionTableResult: DecisionTableResult): List<HtmlResult?> {
        val (headers, rows, tableResult) = decisionTableResult
        val name = decisionTableResult.decisionTable.description.name
        val desc = decisionTableResult.decisionTable.description.descriptiveText

        val htmlDescription = if (desc != "")
            description {
                paragraphs(desc.split("\n"))
            }
        else
            null

        return listOf(
            title(name),
            htmlDescription,
            table(renderContext, tableResult, headers.size) {
                headers(headers)
                rows(rows)
            }
        )
    }

    private fun handleScenarioResult(scenarioResult: ScenarioResult): List<HtmlResult?> {
        val name = scenarioResult.scenario.description.name
        val desc = scenarioResult.scenario.description.descriptiveText

        val htmlDescription = if (desc != "")
            description {
                paragraphs(desc.split("\n"))
            }
        else
            null

        return listOf(
            title(name),
            htmlDescription,
            list {
                steps(scenarioResult.steps)
            }
        )
    }

    private fun title(value: String?): HtmlTitle? {
        return if (value != null) HtmlTitle(value) else null
    }

    private fun description(block: HtmlDescription.() -> Unit): HtmlDescription? {
        val htmlDescription = HtmlDescription()
        htmlDescription.block()
        return htmlDescription
    }

    private fun table(
        renderContext: HtmlRenderContext,
        tableStatus: Status,
        columnCount: Int,
        block: HtmlTable.() -> Unit
    ): HtmlTable {
        val table = HtmlTable(renderContext, tableStatus, columnCount)
        table.block()
        return table
    }

    private fun list(block: HtmlList.() -> Unit): HtmlList {
        val htmlList = HtmlList()
        htmlList.block()
        return htmlList
    }
}
