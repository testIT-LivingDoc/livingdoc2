package org.livingdoc.repositories.file

import org.livingdoc.repositories.Document
import org.livingdoc.repositories.DocumentRepository

class FileRepository(
        private val name: String,
        private val config: FileRepositoryConfig,
        private val fileResolver: FileResolver = FileResolver()
) : DocumentRepository {

    override fun getDocument(documentIdentifier: String): Document {
        val file = fileResolver.resolveFile(config.documentRoot, documentIdentifier)
        return file.format().parse(file.stream())
    }
}
