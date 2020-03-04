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
import org.livingdoc.reports.confluence.tree.elements.cfHeaders
import org.livingdoc.reports.confluence.tree.elements.cfReportRow
import org.livingdoc.reports.confluence.tree.elements.cfRows
import org.livingdoc.reports.confluence.tree.elements.cfSteps
import org.livingdoc.reports.confluence.tree.elements.cfTagRow
import org.livingdoc.reports.html.elements.HtmlDescription
import org.livingdoc.reports.html.elements.HtmlElement
import org.livingdoc.reports.html.elements.HtmlList
import org.livingdoc.reports.html.elements.HtmlTable
import org.livingdoc.reports.html.elements.HtmlTitle
import org.livingdoc.reports.html.elements.paragraphs
import org.livingdoc.reports.html.elements.summaryTableHeader
import org.livingdoc.reports.spi.Format
import org.livingdoc.reports.spi.ReportRenderer
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import org.livingdoc.results.examples.scenarios.ScenarioResult

private val VERSION_SEPERATOR = '@'

@Format("confluence-page-tree")
class ConfluencePageTreeReportRenderer : ReportRenderer {

    override fun render(documentResults: List<DocumentResult>, config: Map<String, Any>) {
        val confluenceConfig = YamlUtils.toObject(config, ConfluencePageTreeReportConfig::class)

        executeWithResourceService(confluenceConfig) { service ->
            val root = findRootPage(service, confluenceConfig)
            val prevPageMapping = findPreviousPages(root, documentResults, service)

            val reports = documentResults.map { documentResult ->
                documentResult to
                        renderReport(documentResult, confluenceConfig, prevPageMapping[documentResult], root, service)
            }

            renderIndex(confluenceConfig, service, root, reports)
        }
    }

    fun findRootPage(service: RemoteContentService, config: ConfluencePageTreeReportConfig): Content {
        val contentFetcher = service.find().withId(ContentId.of(config.rootContentId))

        return contentFetcher.fetchCompletionStage()
            .toCompletableFuture().get()
            .orElseThrow { Exception("${config.rootContentId}, ${config.baseURL}") }
        // TODO find better exception to throw
    }

    fun findPreviousPages(
        rootPage: Content,
        documentResults: List<DocumentResult>,
        service: RemoteContentService
    ): Map<DocumentResult, Content> {
        // TODO Rewrite with the following line
        // val children = rootPage.descendants[ContentType.PAGE]

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

    fun renderReport(
        documentResult: DocumentResult,
        config: ConfluencePageTreeReportConfig,
        prevPage: Content?,
        rootPage: Content,
        service: RemoteContentService
    ): ContentId {

        // Render report
        val reportBody = render(documentResult)

        // Upload report
        return prevPage?.let {
            updatePage(it, reportBody, service, config)
        } ?: createPage(rootPage, documentResult, reportBody, service)
    }

    fun render(documentResult: DocumentResult): String {
        return documentResult.results
            .flatMap { result ->
                when (result) {
                    is DecisionTableResult -> handleDecisionTableResult(result)
                    is ScenarioResult -> handleScenarioResult(result)
                    else -> throw IllegalArgumentException("Unknown Result type.")
                }
            }
            .filterNotNull()
            .joinToString("\n")
    }

    fun renderIndex(
        config: ConfluencePageTreeReportConfig,
        service: RemoteContentService,
        rootPage: Content,
        reports: List<Pair<DocumentResult, ContentId>>
    ) {
        val reportsByTag = reports.flatMap { report ->
            listOf(
                listOf("all" to report),
                report.first.tags.map { tag ->
                    tag to report
                }
            ).flatten()
        }.groupBy({ it.first }, { it.second })

        val tagSummary = HtmlTable {
            summaryTableHeader()

            reportsByTag.map { (tag, documentResults) ->
                cfTagRow(tag, documentResults)
                cfReportRow(tag, documentResults)
            }
        }

        updatePage(rootPage, tagSummary.toString(), service, config)
    }

    private fun handleDecisionTableResult(decisionTableResult: DecisionTableResult): List<HtmlElement?> {
        val (headers, rows, tableResult) = decisionTableResult
        val name = decisionTableResult.decisionTable.description.name
        val desc = decisionTableResult.decisionTable.description.descriptiveText

        return listOf(
            HtmlTitle(name),
            HtmlDescription {
                paragraphs(desc.split("\n"))
            },
            HtmlTable {
                cfHeaders(headers)
                cfRows(rows)
            }
        )
    }

    private fun handleScenarioResult(scenarioResult: ScenarioResult): List<HtmlElement?> {
        val name = scenarioResult.scenario.description.name
        val desc = scenarioResult.scenario.description.descriptiveText

        return listOf(
            HtmlTitle(name),
            HtmlDescription {
                paragraphs(desc.split("\n"))
            },
            HtmlList {
                cfSteps(scenarioResult.steps)
            }
        )
    }

    fun createPage(
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

    fun updatePage(
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

    fun executeWithResourceService(
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
