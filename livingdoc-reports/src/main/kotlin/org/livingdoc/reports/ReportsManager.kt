package org.livingdoc.reports

import org.livingdoc.config.ConfigProvider
import org.livingdoc.reports.config.ReportDefinition
import org.livingdoc.reports.config.ReportsConfig
import org.livingdoc.reports.spi.Format
import org.livingdoc.reports.spi.ReportRenderer
import org.livingdoc.results.documents.DocumentResult
import java.util.*
import kotlin.reflect.full.findAnnotation

class ReportsManager(
    private val config: ReportsConfig,
    private val serviceLoader: ServiceLoader<ReportRenderer>
) {
    fun generateReports(result: DocumentResult) {
        val reports = getActivatedReports()
        for (report in reports) {
            val renderer = getReportRenderer(report.format)
            renderer.render(result, report.config)
        }
    }

    private fun getActivatedReports(): List<ReportDefinition> {
        val includedReports = System.getProperty("livingdoc.reports.include")?.split(",")
        return includedReports?.let { config.reports.filter { includedReports.contains(it.name) } } ?: config.reports
    }

    private fun getReportRenderer(format: String): ReportRenderer {
        return serviceLoader.find {
            val annotation = it.javaClass.kotlin.findAnnotation<Format>()
                ?: throw MissingFormatAnnotationException(
                    "The ReportRenderer ${it.javaClass.name} leak the required Format Annotation."
                )
            format == annotation.value
        } ?: throw ReportFormatNotFoundException("No ReportRenderer with format $format found.")
    }

    companion object {
        fun from(configProvider: ConfigProvider): ReportsManager {
            val config = ReportsConfig.from(configProvider)

            val serviceLoader = ServiceLoader.load(ReportRenderer::class.java)
            return ReportsManager(config, serviceLoader)
        }
    }
}

class MissingFormatAnnotationException(message: String) : IllegalArgumentException(message)

class ReportFormatNotFoundException(message: String) : IllegalArgumentException(message)
