package org.livingdoc.reports.confluence_tree

data class ConfluencePageTreeReportConfig(
    var repositoryName: String = "",
    var rootPage: String = "",
    var baseURL: String = "",
    var path: String = "",
    var username: String = "",
    var password: String = "",
    var comment: String = ""
)
