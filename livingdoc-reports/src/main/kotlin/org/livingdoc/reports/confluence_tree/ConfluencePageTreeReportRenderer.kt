package org.livingdoc.reports.confluence_tree

import com.atlassian.confluence.rest.client.RestClientFactory
import com.atlassian.confluence.rest.client.authentication.AuthenticatedWebResourceProvider
import org.livingdoc.config.YamlUtils
import org.livingdoc.reports.spi.Format
import org.livingdoc.reports.spi.ReportRenderer
import org.livingdoc.results.documents.DocumentResult

@Format("confluence-page-tree")
class ConfluencePageTreeReportRenderer: ReportRenderer {

    override fun render(documentResults: List<DocumentResult>, config: Map<String, Any>) {
        val confluenceConfig = YamlUtils.toObject(config, ConfluencePageTreeReportConfig::class)

        documentResults.forEach {
            renderReport(it, confluenceConfig)
        }
    }

    fun renderReport(documentResult: DocumentResult, config: ConfluencePageTreeReportConfig) {
        TODO("Find the corresponding pagetree in confluence")
        TODO("Generate the report")
        TODO("Upload the report as a confluence page")
    }

    fun createOrFindPageTree(config: ConfluencePageTreeReportConfig) {
        AuthenticatedWebResourceProvider(
            RestClientFactory.newClient(),
            config.baseURL,
            config.path
        ).also {
            it.setAuthContext(
                config.username, config.password.toCharArray()
            )
        }.use { authenticatedWebResourceProvider ->
            TODO("Locate page tree root")
        }
    }

    fun createOrFindPage(documentResult: DocumentResult) {

    }

}
