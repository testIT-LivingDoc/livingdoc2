package org.livingdoc.repositories.rest

import org.livingdoc.repositories.DocumentNotFoundException

class RESTDocumentNotFoundException : DocumentNotFoundException {
    constructor(id: String, url: String) : super("Could not find document with ID [$id] on server [$url]!")
    constructor(
        throwable: Throwable,
        id: String,
        url: String
    ) : super("Could not find document with ID [$id] on server [$url]!", throwable)
}
