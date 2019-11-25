package org.livingdoc.repositories.confluence

import com.atlassian.confluence.api.model.Expansion
import com.atlassian.confluence.api.model.Expansions
import com.atlassian.confluence.api.model.content.Content
import com.atlassian.confluence.api.model.content.ContentRepresentation
import com.atlassian.confluence.api.model.content.id.ContentId
import com.atlassian.confluence.rest.client.RemoteContentService
import com.atlassian.confluence.rest.client.RemoteContentServiceImpl
import com.atlassian.confluence.rest.client.RestClientFactory
import com.atlassian.confluence.rest.client.authentication.AuthenticatedWebResourceProvider
import com.google.common.util.concurrent.MoreExecutors
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.DocumentRepository
import org.livingdoc.repositories.format.HtmlFormat
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ExecutionException

/**
 * This implementation of a DocumentRepository uses a Confluence server to get the Documents from.
 * The remote Confluence server can be configured in the livingdoc.yml config section of the repository.
 */
class ConfluenceRepository(
    private val name: String,
    private val config: ConfluenceRepositoryConfig
) : DocumentRepository {
    private val client: RemoteContentService

    init {
        val authenticatedWebResourceProvider = AuthenticatedWebResourceProvider(
            RestClientFactory.newClient(),
            config.baseURL,
            config.path
        )
        authenticatedWebResourceProvider.setAuthContext(config.username, config.password.toCharArray())
        client = RemoteContentServiceImpl(
            authenticatedWebResourceProvider,
            MoreExecutors.newDirectExecutorService()
        )
    }

    override fun getDocument(documentIdentifier: String): Document {
        val content =
            try {
                client.find(
                    Expansion(
                        Content.Expansions.BODY,
                        Expansions(Expansion("storage", Expansions(Expansion("content"))))
                    )
                ).withId(ContentId.valueOf(documentIdentifier)).fetchCompletionStage()
                    .toCompletableFuture().get()
                    .orElseThrow { ConfluenceDocumentNotFoundException(documentIdentifier, config.baseURL) }
            } catch (e: ExecutionException) {
                throw ConfluenceDocumentNotFoundException(e, documentIdentifier, config.baseURL)
            }
        return parse(content)
    }

    private fun parse(content: Content): Document {
        val body = content.body[ContentRepresentation.STORAGE]
            ?: throw IllegalArgumentException("Content must contain the storage representation")
        val value = body.value

        val htmlFormat = HtmlFormat()
        return htmlFormat.parse(value.byteInputStream(StandardCharsets.UTF_8))
    }
}
