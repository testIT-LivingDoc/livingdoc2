package org.livingdoc.reports.html

import org.livingdoc.config.YamlUtils
import org.livingdoc.reports.ReportWriter
import org.livingdoc.reports.html.elements.HtmlColumnLayout
import org.livingdoc.reports.html.elements.HtmlErrorContext
import org.livingdoc.reports.html.elements.HtmlFooter
import org.livingdoc.reports.html.elements.indexList
import org.livingdoc.reports.html.elements.populateFooter
import org.livingdoc.reports.html.elements.report
import org.livingdoc.reports.html.elements.tagList
import org.livingdoc.reports.spi.Format
import org.livingdoc.reports.spi.ReportRenderer
import org.livingdoc.results.documents.DocumentResult
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val MILLISECONDS_DIVIDER = 1000f

@Format("html")
class HtmlReportRenderer : ReportRenderer {

    private val renderContext = HtmlErrorContext()

    override fun render(documentResults: List<DocumentResult>, config: Map<String, Any>) {
        val htmlConfig = YamlUtils.toObject(config, HtmlReportConfig::class)
        val outputFolder = Paths.get(
            htmlConfig.outputDir,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
        ).toString()
        val reportWriter = ReportWriter(outputFolder, fileExtension = "html")

        val cssWriter = ReportWriter(outputFolder, fileExtension = "css")
        val jsWriter = ReportWriter(outputFolder, fileExtension = "js")

        cssWriter.writeToFile(
            reportStyle(),
            "style"
        )
        jsWriter.writeToFile(
            reportScript(),
            "script"
        )

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

        val columnContainer = HtmlColumnLayout {
            report(documentResult, renderContext)
        }

        val footer = HtmlFooter {
            populateFooter()
        }

        return HtmlReportTemplate()
            .renderElementListTemplate(listOf(columnContainer, footer), renderContext)
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

        val footer = HtmlFooter {
            populateFooter()
        }

        return HtmlReportTemplate()
            .renderElementListTemplate(listOf(columnContainer, footer), renderContext)
    }
}
