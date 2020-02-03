package org.livingdoc.reports.confluence

/**
 * The configuration object for the Confluence Report.
 *
 * @param baseURL The baseURL of the Confluence Server with the format `protocol://host:port`
 * @param path The Context path of the Confluence Server, for `/`
 * @param username The username of a confluence user with access to the Executable Documents.
 * @param password The password of the confluence user given by username.
 */
data class ConfluenceReportConfig(
    var baseURL: String = "",
    var path: String = "",
    var username: String = "",
    var password: String = "",
    var comment: String = "",
    var environment: String = "",
    var minoredit: Boolean = false
)
