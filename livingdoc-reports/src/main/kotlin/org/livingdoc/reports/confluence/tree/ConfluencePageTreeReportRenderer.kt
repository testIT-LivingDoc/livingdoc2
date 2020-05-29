package org.livingdoc.reports.confluence.tree

import com.atlassian.confluence.api.model.Expansion
import com.atlassian.confluence.api.model.content.Content
import com.atlassian.confluence.api.model.content.ContentRepresentation
import com.atlassian.confluence.api.model.content.ContentStatus
import com.atlassian.confluence.api.model.content.ContentType
import com.atlassian.confluence.api.model.content.id.ContentId
import com.atlassian.confluence.rest.client.RemoteContentService
import com.atlassian.confluence.rest.client.RemoteContentServiceImpl
import com.atlassian.confluence.rest.client.RestClientFactory
import com.atlassian.confluence.rest.client.authentication.AuthenticatedWebResourceProvider
import com.google.common.util.concurrent.MoreExecutors
import org.livingdoc.config.YamlUtils
import org.livingdoc.reports.confluence.tree.elements.ConfluenceIndex
import org.livingdoc.reports.confluence.tree.elements.ConfluenceReport
import org.livingdoc.reports.spi.Format
import org.livingdoc.reports.spi.ReportRenderer
import org.livingdoc.results.documents.DocumentResult

private val VERSION_SEPERATOR = '@'

/**
 * A [ReportRenderer] that creates confluence pages under a given index page.
 */
@Format("confluence-page-tree")
class ConfluencePageTreeReportRenderer : ReportRenderer {
    override fun render(documentResults: List<DocumentResult>, config: Map<String, Any>) {
        val confluenceConfig = YamlUtils.toObject(config, ConfluencePageTreeReportConfig::class)

        executeWithContentService(confluenceConfig) { service ->
            val root = findRootPage(service, confluenceConfig)
            val prevPageMapping = findPreviousPages(root, documentResults, service)

            val reports = documentResults.map { documentResult ->
                documentResult to
                        renderReport(documentResult, confluenceConfig, prevPageMapping[documentResult], root, service)
            }

            renderIndex(confluenceConfig, service, root, reports)
        }
    }

    private fun findRootPage(service: RemoteContentService, config: ConfluencePageTreeReportConfig): Content {
        val contentFetcher = service.find().withId(ContentId.of(config.rootContentId))

        return contentFetcher.fetchCompletionStage()
            .toCompletableFuture().get()
            .orElseThrow { Exception("${config.rootContentId}, ${config.baseURL}") }
        // TODO find better exception to throw
    }

    private fun findPreviousPages(
        rootPage: Content,
        documentResults: List<DocumentResult>,
        service: RemoteContentService
    ): Map<DocumentResult, Content> {
        return documentResults.map { documentResult ->
            documentResult to
                    service
                        .find(Expansion("version"))
                        .withSpace(rootPage.space)
                        .withType(ContentType.PAGE)
                        .withTitle(documentResult.documentClass.name)
                        .fetchCompletionStage()
                        .toCompletableFuture()
                        .get()
                        .orElse(null)
        }.toMap()
    }

    private fun renderReport(
        documentResult: DocumentResult,
        config: ConfluencePageTreeReportConfig,
        prevPage: Content?,
        rootPage: Content,
        service: RemoteContentService
    ): ContentId {
        // Render report
        val reportBody = ConfluenceReport(documentResult).toString()

        // Upload report
        return prevPage?.let {
            updatePage(it, reportBody, service, config)
        } ?: createPage(rootPage, documentResult, reportBody, service)
    }

    private fun renderIndex(
        config: ConfluencePageTreeReportConfig,
        service: RemoteContentService,
        rootPage: Content,
        reports: List<Pair<DocumentResult, ContentId>>
    ) {
        val tagSummary = renderIndex(reports)

        updatePage(rootPage, tagSummary, service, config)
    }

    fun renderIndex(reports: List<Pair<DocumentResult, ContentId>>): String {
        return ConfluenceIndex(reports.map { it.first }).toString()
    }

    private fun createPage(
        rootPage: Content,
        documentResult: DocumentResult,
        reportBody: String,
        service: RemoteContentService
    ): ContentId {

        val newPage =
            Content.builder()
                .type(ContentType.PAGE)
                .status(ContentStatus.CURRENT)
                .parent(rootPage)
                .space(rootPage.space)
                .title(documentResult.documentClass.name)
                .body(reportBody, ContentRepresentation.STORAGE)
                .build()

        return service.createCompletionStage(newPage)
            .toCompletableFuture()
            .get()
            .id
    }

    private fun updatePage(
        prevPage: Content,
        reportBody: String,
        service: RemoteContentService,
        config: ConfluencePageTreeReportConfig
    ): ContentId {

        print(prevPage.version)
        val newPage =
            Content.builder(prevPage)
                .status(ContentStatus.CURRENT)
                .version(
                    prevPage
                        .version
                        .nextBuilder()
                        .message(config.comment)
                        .build()
                )
                .body(reportBody, ContentRepresentation.STORAGE)
                .build()

        return service.updateCompletionStage(newPage)
            .toCompletableFuture()
            .get()
            .id
    }

    private fun executeWithContentService(
        config: ConfluencePageTreeReportConfig,
        instructions: (service: RemoteContentService) -> Unit
    ) {
        AuthenticatedWebResourceProvider(
            RestClientFactory.newClient(),
            config.baseURL,
            config.path
        ).also {
            it.setAuthContext(
                config.username, config.password.toCharArray()
            )
        }.use {
            val service = RemoteContentServiceImpl(
                it,
                MoreExecutors.newDirectExecutorService()
            )

            instructions(service)
        }
    }
}

fun RemoteContentService.RemoteContentFinder.withStringId(identifier: String):
        RemoteContentService.RemoteSingleContentFetcher {
    return if (identifier.contains(VERSION_SEPERATOR)) {
        val docParams = identifier.split(VERSION_SEPERATOR)
        val docId = docParams[0]
        if (docParams.size != 2) {
            throw IllegalArgumentException("The given id is not a confluence content id $identifier")
        }
        val docVersion = docParams[1].toInt()

        this.withIdAndVersion(ContentId.valueOf(docId), docVersion)
    } else {
        this.withId(ContentId.valueOf(identifier))
    }
}
