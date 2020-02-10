package org.livingdoc.reports.html

data class HtmlReportConfig(
    var outputDir: String = "livingdoc/reports/html",
    var generateIndex: Boolean = false
)
