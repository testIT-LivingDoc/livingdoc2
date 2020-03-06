package org.livingdoc.reports.confluence.attachment

import com.atlassian.confluence.api.model.content.AttachmentUpload
import com.atlassian.confluence.api.model.content.id.ContentId
import com.atlassian.confluence.rest.client.RemoteAttachmentServiceImpl
import com.atlassian.confluence.rest.client.RestClientFactory
import com.atlassian.confluence.rest.client.authentication.AuthenticatedWebResourceProvider
import com.google.common.util.concurrent.MoreExecutors
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.config.YamlUtils
import org.livingdoc.reports.html.HtmlReportRenderer
import org.livingdoc.reports.spi.Format
import org.livingdoc.reports.spi.ReportRenderer
import org.livingdoc.results.documents.DocumentResult
import java.lang.IllegalArgumentException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.time.ZonedDateTime

private const val MINOR_VERSION = false

@Format("confluence-attachment")
class ConfluenceAttachmentReportRenderer : ReportRenderer {

    override fun render(documentResults: List<DocumentResult>, config: Map<String, Any>) {
        val confluenceConfig = YamlUtils.toObject(config, ConfluenceAttachmentReportConfig::class)

        documentResults.forEach {
            renderReport(it, confluenceConfig)
        }
    }

    internal fun renderReport(documentResult: DocumentResult, config: ConfluenceAttachmentReportConfig) {
        val repositoryName = extractRepositoryName(documentResult)

        // Check for matching repository; only generate report on match
        if (repositoryName != config.repositoryName) {
            return
        }

        // Render html report
        val html = HtmlReportRenderer().render(documentResult)

        // Upload report to confluence

        val contentId = extractContentId(documentResult)

        uploadReport(html, contentId, config)
    }

    /**
     * Uploads a report to a conflucne page as an attachment
     *
     * @param report The report text to upload
     * @param contentId The [ContentId] of the page to attach the report to
     * @param config A [ConfluenceAttachmentReportConfig] containing further settings for the upload
     */
    internal fun uploadReport(report: String, contentId: ContentId, config: ConfluenceAttachmentReportConfig) {

        val authenticatedWebResourceProvider = AuthenticatedWebResourceProvider(
            RestClientFactory.newClient(),
            config.baseURL,
            config.path
        )
        authenticatedWebResourceProvider.setAuthContext(
            config.username, config.password.toCharArray()
        )

        val contentFile = Files.createTempFile(config.filename, null)
        Files.write(contentFile, report.toByteArray(StandardCharsets.UTF_8))

        val comment = if (config.comment.isNotEmpty()) {
            config.comment
        } else {
            "Report from " + ZonedDateTime.now().toString()
        }

        val attachment = RemoteAttachmentServiceImpl(
            authenticatedWebResourceProvider, MoreExecutors.newDirectExecutorService()
        )
        val atUp = AttachmentUpload(
            contentFile.toFile(), config.filename, "text/html",
            comment, MINOR_VERSION
        )

        // Look for already existing attachment
        val attachmentId = attachment
            .find()
            .withContainerId(contentId)
            .withFilename(config.filename)
            .fetchCompletionStage()
            .toCompletableFuture()
            .get()
            .orElseGet { null }
            ?.id

        if (attachmentId == null) {
            // Add new attachment
            attachment.addAttachmentsCompletionStage(contentId, listOf(atUp))
        } else {
            // Update existing attachment
            attachment.updateDataCompletionStage(attachmentId, atUp)
        }
    }

    internal fun extractContentId(documentResult: DocumentResult): ContentId {
        try {
            val testAnnotation = documentResult.documentClass
                .getAnnotation(ExecutableDocument::class.java).value
            // Extract the content id from the page link
            val numId = Regex("(?<=://)[0-9]+").find(testAnnotation)!!.groupValues[0].toLong()

            return ContentId.of(numId)
        } catch (e: Exception) {
            throw IllegalArgumentException("No content id could be extracted form the given document")
        }
    }

    internal fun extractRepositoryName(documentResult: DocumentResult): String {
        try {
            val testAnnotation = documentResult.documentClass
                .getAnnotation(ExecutableDocument::class.java).value

            return Regex("^.*(?=://)").find(testAnnotation)!!.groupValues[0]
        } catch (e: Exception) {
            throw IllegalArgumentException("No repository name could be extracted form the given document")
        }
    }
}
