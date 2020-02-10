package org.livingdoc.engine

import org.livingdoc.results.documents.DocumentResult

interface DocumentFixture {
    /**
     * Execute runs the executable document described by this DocumentFixture
     *
     * @return a [DocumentResult] for this execution
     */
    fun execute(): DocumentResult
}
