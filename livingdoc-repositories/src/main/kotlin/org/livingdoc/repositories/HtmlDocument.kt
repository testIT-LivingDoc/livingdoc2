package org.livingdoc.repositories

class HtmlDocument(
        content: List<DocumentNode>,
        val jsoupDoc: org.jsoup.nodes.Document
) : Document(content)
