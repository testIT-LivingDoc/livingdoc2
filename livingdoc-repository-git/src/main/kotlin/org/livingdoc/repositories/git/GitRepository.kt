package org.livingdoc.repositories.git

import org.livingdoc.repositories.Document
import org.livingdoc.repositories.DocumentRepository
import org.livingdoc.repositories.format.DocumentFormatManager

/**
 * GitRepository can find [Documents][Document] in remote git repositories
 */
class GitRepository(val name: String, config: GitRepositoryConfig) : DocumentRepository {
    private val fileResolver = GitFileResolver(GitRepositoryResolver(config).resolve())

    override fun getDocument(documentIdentifier: String): Document {
        val identifier = GitDocumentIdentifier.parse(documentIdentifier)

        val file = fileResolver.resolve(identifier)

        return DocumentFormatManager.getFormat(identifier.format).parse(file)
    }
}
