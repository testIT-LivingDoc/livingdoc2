package org.livingdoc.repositories

import org.livingdoc.repositories.model.TestData

/**
 * Document represents the [TestData] contained in a document fetched from a [DocumentRepository].
 *
 * @see DocumentRepository
 * @see TestData
 */
open class Document(val elements: List<TestData>)
