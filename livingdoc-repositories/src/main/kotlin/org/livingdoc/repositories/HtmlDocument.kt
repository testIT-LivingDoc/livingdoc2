package org.livingdoc.repositories

import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.scenario.Scenario

class HtmlDocument(
        tables: List<DecisionTable>,
        lists: List<Scenario>,
        val jsoupDoc: org.jsoup.nodes.Document
) : Document(tables, lists)
