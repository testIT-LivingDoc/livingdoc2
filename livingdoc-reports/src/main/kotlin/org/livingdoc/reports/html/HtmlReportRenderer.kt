package org.livingdoc.reports.html

import org.livingdoc.config.YamlUtils
import org.livingdoc.reports.ReportWriter
import org.livingdoc.reports.spi.Format
import org.livingdoc.reports.spi.ReportRenderer
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import org.livingdoc.results.examples.scenarios.ScenarioResult
import java.nio.file.Paths
import java.util.*

@Format("html")
class HtmlReportRenderer : ReportRenderer {

    private val renderContext = HtmlRenderContext()

    override fun render(documentResults: List<DocumentResult>, config: Map<String, Any>) {
        val htmlConfig = YamlUtils.toObject(config, HtmlReportConfig::class)
        val outputFolder = Paths.get(htmlConfig.outputDir, UUID.randomUUID().toString()).toString()

        documentResults.forEach { documentResult ->
            val html = render(documentResult)
            ReportWriter(outputFolder, fileExtension = "html").writeToFile(
                html,
                documentResult.documentClass.name
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
            .renderTemplate(htmlResults, renderContext)
    }

    fun renderAggregatedReport() {

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
