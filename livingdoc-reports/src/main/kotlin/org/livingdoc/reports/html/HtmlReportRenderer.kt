package org.livingdoc.reports.html

import org.livingdoc.config.YamlUtils
import org.livingdoc.reports.ReportWriter
import org.livingdoc.reports.html.elements.HtmlColumnLayout
import org.livingdoc.reports.html.elements.HtmlDescription
import org.livingdoc.reports.html.elements.HtmlElement
import org.livingdoc.reports.html.elements.HtmlList
import org.livingdoc.reports.html.elements.HtmlRenderContext
import org.livingdoc.reports.html.elements.HtmlTable
import org.livingdoc.reports.html.elements.HtmlTitle
import org.livingdoc.reports.html.elements.headers
import org.livingdoc.reports.html.elements.indexList
import org.livingdoc.reports.html.elements.paragraphs
import org.livingdoc.reports.html.elements.rows
import org.livingdoc.reports.html.elements.steps
import org.livingdoc.reports.html.elements.tagList
import org.livingdoc.reports.spi.Format
import org.livingdoc.reports.spi.ReportRenderer
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
     *
     * @param documentResult The document that should be used for the report
     * @return the HTML code for a single report as a String
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
     *
     * @param reports a list of all reports that were generated in this test run
     * @return a String containing HTML code of the two column layout
     */
    private fun renderIndex(reports: List<Pair<DocumentResult, Path>>): String {

        val columnContainer = HtmlColumnLayout {
            indexList(reports)
            tagList(reports)
        }

        return HtmlReportTemplate()
            .renderElementTemplate(columnContainer, renderContext)
    }

    private fun handleDecisionTableResult(decisionTableResult: DecisionTableResult): List<HtmlElement?> {
        val (headers, rows, tableResult) = decisionTableResult
        val name = decisionTableResult.decisionTable.description.name
        val desc = decisionTableResult.decisionTable.description.descriptiveText

        return listOf(
            HtmlTitle(name),
            HtmlDescription {
                paragraphs(desc.split("\n"))
            },
            HtmlTable(renderContext, tableResult, headers.size) {
                headers(headers)
                rows(rows)
            }
        )
    }

    private fun handleScenarioResult(scenarioResult: ScenarioResult): List<HtmlElement?> {
        val name = scenarioResult.scenario.description.name
        val desc = scenarioResult.scenario.description.descriptiveText

        return listOf(
            HtmlTitle(name),
            HtmlDescription {
                paragraphs(desc.split("\n"))
            },
            HtmlList {
                steps(scenarioResult.steps)
            }
        )
    }
}
