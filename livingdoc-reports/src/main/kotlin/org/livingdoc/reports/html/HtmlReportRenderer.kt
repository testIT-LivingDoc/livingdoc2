package org.livingdoc.reports.html

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
            .renderTemplate(htmlResults, renderContext)
    }

    private fun renderIndex(reports: List<Pair<DocumentResult, Path>>): String {
        val htmlResults = reports.map {
            titleLink(
                it.first.documentClass.name,
                it.second.fileName.toString(),
                it.first.documentStatus
            )
        }

        return HtmlReportTemplate()
            .renderTemplate(htmlResults, renderContext)
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

    private fun titleLink(
        value: String,
        linkAddress: String,
        status: Status
    ): HtmlTitle {
        return HtmlTitle(
            HtmlLink(
                value, linkAddress, status
            ).toString()
        )
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
