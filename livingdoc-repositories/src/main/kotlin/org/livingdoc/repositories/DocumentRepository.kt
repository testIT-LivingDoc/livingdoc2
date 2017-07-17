package org.livingdoc.repositories

interface DocumentRepository {

    fun getDocument(documentIdentifier: String): Document?

}