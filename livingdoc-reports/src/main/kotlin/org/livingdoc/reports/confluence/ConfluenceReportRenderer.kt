package org.livingdoc.reports.confluence

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
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.time.ZonedDateTime

private const val MINOR_VERSION = false

@Format("confluence")
class ConfluenceReportRenderer : ReportRenderer {

    override fun render(documentResult: DocumentResult, config: Map<String, Any>) {
        // Render html report
        val html = HtmlReportRenderer().render(documentResult)

        // Upload report to confluence
        val confluenceConfig = YamlUtils.toObject(config, ConfluenceReportConfig::class)

        val authenticatedWebResourceProvider = AuthenticatedWebResourceProvider(
            RestClientFactory.newClient(),
            confluenceConfig.baseURL,
            confluenceConfig.path
        )
        authenticatedWebResourceProvider.setAuthContext(
            confluenceConfig.username, confluenceConfig.password.toCharArray()
        )

        val testAnnotation = documentResult.documentClass
            .getAnnotation(ExecutableDocument::class.java).value
        // Extract the content id from the page link
        val contentId = Regex("(?<=://)[0-9]+").find(testAnnotation)!!.groupValues[0].toLong()

        val contentFile = Files.createTempFile(confluenceConfig.filename, null)
        Files.write(contentFile, html.toByteArray(StandardCharsets.UTF_8))

        val comment = if (confluenceConfig.comment.isNotEmpty()) {
            confluenceConfig.comment
        } else {
            "Report from " + ZonedDateTime.now().toString()
        }

        val attachment = RemoteAttachmentServiceImpl(
            authenticatedWebResourceProvider, MoreExecutors.newDirectExecutorService()
        )
        val atUp = AttachmentUpload(
            contentFile.toFile(), confluenceConfig.filename, "text/html",
            comment, MINOR_VERSION
        )

        // Look for already existing attachment
        val attachementId = attachment
            .find()
            .withContainerId(ContentId.of(contentId))
            .withFilename(confluenceConfig.filename)
            .fetchCompletionStage()
            .toCompletableFuture()
            .get()
            .orElseGet { null }
            ?.id

        if (attachementId == null) {
            // Add new attachment
            attachment.addAttachmentsCompletionStage(ContentId.of(contentId), listOf(atUp))
        } else {
            // Update existing attachment
            attachment.updateDataCompletionStage(attachementId, atUp)
        }
    }
}
