package org.livingdoc.repositories.confluence

import org.livingdoc.repositories.DocumentNotFoundException

class ConfluenceDocumentNotFoundException : DocumentNotFoundException {
    constructor(id: String, url: String) : super("Could not find document with ID [$id] on server [$url]!")
    constructor(
        throwable: Throwable,
        id: String,
        url: String
    ) : super("Could not find document with ID [$id] on server [$url]!", throwable)
}
