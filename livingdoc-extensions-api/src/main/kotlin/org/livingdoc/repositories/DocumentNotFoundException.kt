package org.livingdoc.repositories

/**
 * A DocumentNotFoundException is thrown when a [DocumentRepository] cannot find an executable document.
 *
 * @see DocumentRepository
 */
open class DocumentNotFoundException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause)
