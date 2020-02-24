package org.livingdoc.repositories

/**
 * A [DocumentRepository] can look up [Documents][Document] from some storage location.
 *
 * @see Document
 */
interface DocumentRepository {

    /**
     *
     * @param documentIdentifier the identifier for the [Document]
     * @return the selected [Document]
     * @throws DocumentNotFoundException if documentIdentifier cannot be found in the repository
     */
    fun getDocument(documentIdentifier: String): Document
}
