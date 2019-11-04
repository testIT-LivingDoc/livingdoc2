package org.livingdoc.engine.reporting

import com.beust.klaxon.JsonObject
import org.livingdoc.engine.execution.DocumentResult

import com.beust.klaxon.json
import org.livingdoc.engine.execution.Result
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult

class JsonReportRenderer {
    fun render(documentResult: DocumentResult): String {
        val exampleResults = json {
            obj("results" to array(documentResult.results.map {
                when (it) {
                    is DecisionTableResult -> handleDecisionTableResult(it)
                    is ScenarioResult -> handleScenarioResult(it)
                    else -> throw IllegalArgumentException("Unknown ExampleResult type.")
                }
            }))
        }

        return exampleResults.toJsonString()
    }

    private fun handleDecisionTableResult(decisionTableResult: DecisionTableResult): JsonObject {
        return json {
            obj(
                    "rows" to array(decisionTableResult.rows.map {
                        obj(
                                "fields" to obj(it.headerToField.map { (header, fieldResult) ->
                                    header.name to obj(
                                            "value" to fieldResult.value,
                                            "result" to handleResult(fieldResult.result)
                                    )
                                }),
                                "result" to handleResult(it.result)
                        )
                    }),
                    "result" to handleResult(decisionTableResult.result)
            )
        }
    }

    private fun handleScenarioResult(scenarioResult: ScenarioResult): JsonObject {
        return json {
            obj(
                    "steps" to array(scenarioResult.steps.map {
                        obj(
                                it.value to handleResult(it.result)
                        )
                    }),
                    "result" to handleResult(scenarioResult.result)
            )
        }
    }

    private fun handleResult(result: Result): String {
        return when (result) {
            Result.Executed -> "executed"
            is Result.Disabled -> "disabled"
            Result.Skipped -> "skipped"
            Result.Unknown -> "unknown"
            is Result.Failed -> "failed"
            is Result.Exception -> "exception"
        }
    }
}
