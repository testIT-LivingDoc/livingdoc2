package org.livingdoc.reports.config

data class ReportDefinition(
    var name: String = "",
    var format: String = "",
    var config: Map<String, Any> = emptyMap()
)
