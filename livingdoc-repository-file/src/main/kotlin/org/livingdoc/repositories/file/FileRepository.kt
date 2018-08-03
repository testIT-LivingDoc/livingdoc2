package org.livingdoc.repositories.file

import org.livingdoc.repositories.Document
import org.livingdoc.repositories.DocumentFormat
import org.livingdoc.repositories.DocumentRepository
import java.nio.file.Paths

class FileRepository(
        private val name: String,
        private val config: FileRepositoryConfig
) : DocumentRepository {

    override fun getDocument(documentIdentifier: String): Document {
        // TODO: use DocumentFormatManager to resolve format!
        throw FileDocumentNotFoundException(documentIdentifier, Paths.get(documentIdentifier))
    }

}
