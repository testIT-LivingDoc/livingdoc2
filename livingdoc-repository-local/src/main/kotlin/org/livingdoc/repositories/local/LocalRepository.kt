package org.livingdoc.repositories.local

import org.livingdoc.repositories.Document
import org.livingdoc.repositories.DocumentRepository

class LocalRepository(
        private val name: String,
        private val config: LocalRepositoryConfig
) : DocumentRepository {

    override fun getDocument(documentIdentifier: String): Document? {
        throw IllegalStateException("not implemented")
    }

}