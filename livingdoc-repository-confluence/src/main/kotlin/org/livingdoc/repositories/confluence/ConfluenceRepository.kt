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
    private val visioningSeparator = '@'

    data class ConfluenceIdentifier(val documentId: String, val documentVersion: Int)

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

    /**
     * Get a confluence page as Document. This method uses the xml representation of the confluence page.
     *
     * @param documentIdentifier the DocumentID of the confluence page
     */
    override fun getDocument(documentIdentifier: String): Document {
        val content =
            try {
                val page = client.find(
                    Expansion(
                        Content.Expansions.BODY,
                        Expansions(Expansion("storage", Expansions(Expansion("content"))))
                    )
                )

                val contentFetcher = if (documentIdentifier.contains(visioningSeparator)) {
                    val docParams = getDocumentIdAndVersion(documentIdentifier)
                    page.withIdAndVersion(ContentId.valueOf(docParams.documentId), docParams.documentVersion)
                } else {
                    page.withId(ContentId.valueOf(documentIdentifier))
                }

                contentFetcher.fetchCompletionStage()
                    .toCompletableFuture().get()
                    .orElseThrow { ConfluenceDocumentNotFoundException(documentIdentifier, config.baseURL) }
            } catch (e: ExecutionException) {
                throw ConfluenceDocumentNotFoundException(e, documentIdentifier, config.baseURL)
            }
        return parse(content)
    }

    /**
     * Reads the DocumentId and the Page Version from an documentIdentifier and returns it.
     *
     * @param documentIdentifier The raw DocumentIdentifier containing ID and Version
     * @return A Pair containing the Confluence Page ID and the Page Version
     */
    fun getDocumentIdAndVersion(documentIdentifier: String): ConfluenceIdentifier {
        val docParams = documentIdentifier.split(visioningSeparator)
        val docId = docParams[0]
        if (docParams.size != 2) {
            throw ConfluenceDocumentNotFoundException(docParams.size - 1)
        }
        val docVersion = docParams[1].toInt()

        return ConfluenceIdentifier(docId, docVersion)
    }

    /**
     * Parse the [content] returned by the confluence client library into a Document of LivingDoc
     */
    private fun parse(content: Content): Document {
        val body = content.body[ContentRepresentation.STORAGE]
            ?: throw IllegalArgumentException("Content must contain the storage representation")
        val value = body.value

        val htmlFormat = HtmlFormat()
        return htmlFormat.parse(value.byteInputStream(StandardCharsets.UTF_8))
    }
}
