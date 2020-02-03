package org.livingdoc.reports.confluence

import org.livingdoc.config.YamlUtils
import org.livingdoc.reports.html.HtmlReportRenderer
import org.livingdoc.reports.spi.Format
import org.livingdoc.reports.spi.ReportRenderer
import org.livingdoc.results.documents.DocumentResult

@Format("confluence")
class ConfluenceReportRenderer : ReportRenderer {
    override fun render(documentResult: DocumentResult, config: Map<String, Any>) {
        // Render html report
        val html = HtmlReportRenderer().render(documentResult)

        // Upload report to confluence
        val confluenceConfig = YamlUtils.toObject(config, ConfluenceReportConfig::class)

        TODO("Insert se code of se Sascha")
    }
}
