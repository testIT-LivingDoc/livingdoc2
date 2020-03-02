package org.livingdoc.reports.confluence.tree

data class ConfluencePageTreeReportConfig(
    var rootContentId: Long = 0,
    var baseURL: String = "",
    var path: String = "",
    var username: String = "",
    var password: String = "",
    var comment: String = ""
)
