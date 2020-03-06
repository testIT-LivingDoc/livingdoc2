package org.livingdoc.reports.confluence.attachment

/**
 * The configuration object for the Confluence Report.
 *
 * @param repositoryName The name of the repository for which the report should be generated
 * @param filename The filename the uploaded report should have in confluence
 * @param baseURL The baseURL of the Confluence Server with the format `protocol://host:port`
 * @param path The Context path of the Confluence Server, for `/`
 * @param username The username of a confluence user with access to the Executable Documents.
 * @param password The password of the confluence user given by username.
 * @param comment A comment that is added to the attachment
 */
data class ConfluenceAttachmentReportConfig(
    var repositoryName: String = "",
    var filename: String = "report.html",
    var baseURL: String = "",
    var path: String = "",
    var username: String = "",
    var password: String = "",
    var comment: String = ""
)
