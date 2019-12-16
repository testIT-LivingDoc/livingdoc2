package org.livingdoc.repositories.format

import org.livingdoc.repositories.Document
import org.livingdoc.repositories.model.TestData

/**
 * A HtmlDocument is a [Document] in HTML format.
 *
 * It contains the parsed representation of the HTML DOM tree.
 * @see Document
 */
class HtmlDocument(
    elements: List<TestData>
) : Document(elements)
