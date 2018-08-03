package org.livingdoc.repositories

interface DocumentRepository {

    /**
     *
     * @throws DocumentNotFoundException
     */
    fun getDocument(documentIdentifier: String): Document

}