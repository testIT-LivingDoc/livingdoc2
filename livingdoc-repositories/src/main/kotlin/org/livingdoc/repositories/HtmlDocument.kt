package org.livingdoc.repositories

class HtmlDocument(
    elements: List<Any>,
    val jsoupDoc: org.jsoup.nodes.Document
) : Document(elements)
