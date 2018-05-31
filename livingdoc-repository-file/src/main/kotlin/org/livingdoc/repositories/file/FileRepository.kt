package org.livingdoc.repositories.file

import org.livingdoc.repositories.Document
import org.livingdoc.repositories.DocumentFormat
import org.livingdoc.repositories.DocumentRepository

class FileRepository(
    private val name: String,
    private val format: DocumentFormat,
    private val config: FileRepositoryConfig
) : DocumentRepository {

    override fun getDocument(documentIdentifier: String): Document? {
        throw IllegalStateException("not implemented")
    }

}
