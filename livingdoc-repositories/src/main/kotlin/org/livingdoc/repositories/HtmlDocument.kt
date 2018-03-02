package org.livingdoc.repositories

class HtmlDocument(
        tables: List<DecisionTable>,
        lists: List<Scenario>,
        val jsoupDoc: org.jsoup.nodes.Document
) : Document(tables, lists)
