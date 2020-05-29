package org.livingdoc.repositories.model

import org.livingdoc.repositories.Document

/**
 * TestData models a single example contained inside a [Document]
 *
 * @see Document
 */
interface TestData {
    val description: TestDataDescription
}
