package org.livingdoc.reports.html

import org.livingdoc.config.YamlUtils
import org.livingdoc.engine.execution.documents.DocumentResult
import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.reports.spi.ReportRenderer
import org.livingdoc.reports.ReportWriter
import org.livingdoc.reports.spi.Format
import java.util.*

@Format("html")
class HtmlReportRenderer : ReportRenderer {

    private val renderContext = HtmlRenderContext()

    override fun render(documentResult: DocumentResult, config: Map<String, Any>) {
        val htmlConfig = YamlUtils.toObject(config, HtmlReportConfig::class)
        val html = render(documentResult)
        ReportWriter(htmlConfig.outputDir, fileExtension = "html").writeToFile(
            html,
            "${documentResult.documentClass.name}-${UUID.randomUUID()}"
        )
    }

    fun render(documentResult: DocumentResult): String {
        val exampleResult = documentResult.results

        val htmlResults = exampleResult.map { result ->
            when (result) {
                is DecisionTableResult -> handleDecisionTableResult(result)
                is ScenarioResult -> handleScenarioResult(result)
                else -> throw IllegalArgumentException("Unknown Result type.")
            }
        }

        return HtmlReportTemplate()
            .renderTemplate(htmlResults, renderContext)
    }

    private fun handleDecisionTableResult(decisionTableResult: DecisionTableResult): HtmlTable {
        val (headers, rows, tableResult) = decisionTableResult
        return table(renderContext, tableResult, headers.size) {
            headers(headers)
            rows(rows)
        }
    }

    private fun handleScenarioResult(scenarioResult: ScenarioResult): HtmlList {
        return list {
            steps(scenarioResult.steps)
        }
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
