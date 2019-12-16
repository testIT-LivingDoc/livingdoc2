package org.livingdoc.repositories.format

import org.livingdoc.repositories.Document
import org.livingdoc.repositories.model.TestData

class HtmlDocument(
    elements: List<TestData>,
    val jsoupDoc: org.jsoup.nodes.Document
) : Document(elements)
