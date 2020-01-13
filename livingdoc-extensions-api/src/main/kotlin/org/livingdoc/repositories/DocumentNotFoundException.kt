package org.livingdoc.repositories

open class DocumentNotFoundException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause)
