package org.livingdoc.repositories.file

import org.livingdoc.repositories.Document
import org.livingdoc.repositories.DocumentRepository
import org.livingdoc.repositories.format.DocumentFormatManager

/**
 * A FileRepository is a [DocumentRepository] in a locally mounted filesystem.
 *
 * @see DocumentRepository
 */
class FileRepository(
    private val name: String,
    private val config: FileRepositoryConfig,
    private val fileResolver: FileResolver = FileResolver()
) : DocumentRepository {

    override fun getDocument(documentIdentifier: String): Document {
        val file = fileResolver.resolveFile(config.documentRoot, documentIdentifier)
        return DocumentFormatManager.getFormat(file.extension()).parse(file.stream())
    }
}
