package org.livingdoc.engine

import org.livingdoc.results.documents.DocumentResult

interface Group {
    fun execute(): List<DocumentResult>
}
