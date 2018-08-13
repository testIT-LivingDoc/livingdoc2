package org.livingdoc.engine.reporting

import org.livingdoc.engine.execution.DocumentResult
import org.livingdoc.engine.execution.Result
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import java.lang.RuntimeException

class HtmlReportRenderer {

    private val renderContext = HtmlRenderContext()

    fun render(documentResult: DocumentResult): String {
        val exampleResult = documentResult.results

        val htmlResults = exampleResult.map {
            when (it) {
                is DecisionTableResult -> handleDecisionTableResult(it)
                is ScenarioResult -> handleScenarioResult(it)
                else -> throw RuntimeException("Unknown ExampleResult type.")
            }
        }

        return HtmlReportTemplate().renderTemplate(htmlResults, renderContext)
    }

    private fun handleDecisionTableResult(decisionTableResult: DecisionTableResult): HtmlTable {
        val (headers, rows, tableResult) = decisionTableResult
        return table(renderContext, tableResult, headers.size) {
            headers(headers)
            rows(rows)
        }
    }

    private fun handleScenarioResult(scenarioResult: ScenarioResult): HtmlList {
        return list {
            steps(scenarioResult.steps)
        }
    }

    private fun table(
            renderContext: HtmlRenderContext,
            tableResult: Result,
            columnCount: Int,
            block: HtmlTable.() -> Unit): HtmlTable {
        val table = HtmlTable(renderContext, tableResult, columnCount)
        table.block()
        return table
    }

    private fun list(block: HtmlList.() -> Unit): HtmlList {
        val htmlList = HtmlList()
        htmlList.block()
        return htmlList
    }
}
