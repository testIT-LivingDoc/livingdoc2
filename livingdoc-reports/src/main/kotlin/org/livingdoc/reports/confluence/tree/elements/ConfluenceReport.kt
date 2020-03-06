package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.reports.html.elements.HtmlDescription
import org.livingdoc.reports.html.elements.HtmlList
import org.livingdoc.reports.html.elements.HtmlTable
import org.livingdoc.reports.html.elements.HtmlTitle
import org.livingdoc.reports.html.elements.paragraphs
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.model.TestDataDescription
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import org.livingdoc.results.examples.scenarios.ScenarioResult

/**
 * ConfluenceReport is a [ConfluencePage] that details the results of a [Document] executed as part of a test run.
 */
class ConfluenceReport(documentResult: DocumentResult) : ConfluencePage() {
    init {
        if (documentResult.tags.isNotEmpty()) {
            child {
                ConfluenceStatusBar(documentResult.tags)
            }
        }

        documentResult.results.forEach { result ->
            when (result) {
                is DecisionTableResult -> handleDecisionTableResult(result)
                is ScenarioResult -> handleScenarioResult(result)
                else -> throw IllegalArgumentException("Unknown Result type.")
            }
        }
    }

    private fun handleDecisionTableResult(decisionTableResult: DecisionTableResult) {
        testDataDescription(decisionTableResult.decisionTable.description)

        val (headers, rows, _) = decisionTableResult

        child {
            HtmlTable {
                cfHeaders(headers)
                cfRows(headers, rows)
            }
        }
    }

    private fun handleScenarioResult(scenarioResult: ScenarioResult) {
        testDataDescription(scenarioResult.scenario.description)

        child {
            HtmlList {
                cfSteps(scenarioResult.steps)
            }
        }
    }

    private fun testDataDescription(description: TestDataDescription) {
        child {
            HtmlTitle(description.name)
        }

        child {
            HtmlDescription {
                paragraphs(description.descriptiveText.split("\n"))
            }
        }
    }
}
