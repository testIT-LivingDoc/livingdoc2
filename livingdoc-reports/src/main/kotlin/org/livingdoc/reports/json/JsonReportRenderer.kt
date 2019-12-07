package org.livingdoc.reports.json

import com.beust.klaxon.JsonObject
import org.livingdoc.engine.execution.DocumentResult

import com.beust.klaxon.json
import org.livingdoc.config.YamlUtils
import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.reports.spi.ReportRenderer
import org.livingdoc.reports.ReportWriter
import org.livingdoc.reports.spi.Format
import java.util.*

@Format("json")
class JsonReportRenderer : ReportRenderer {

    override fun render(documentResult: DocumentResult, config: Map<String, Any>) {
        val jsonConfig = YamlUtils.toObject(config, JsonReportConfig::class)

        val json = render(documentResult)
        // TODO get unique name for reports
        ReportWriter(jsonConfig.outputDir, fileExtension = "json").writeToFile(json, UUID.randomUUID().toString())
    }

    fun render(documentResult: DocumentResult): String {
        val exampleResults = json {
            obj("results" to array(documentResult.results.map {
                when (it) {
                    is DecisionTableResult -> handleDecisionTableResult(it)
                    is ScenarioResult -> handleScenarioResult(it)
                    else -> throw IllegalArgumentException("Unknown Result type.")
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
                                "status" to handleResult(fieldResult.status)
                            )
                        }),
                        "status" to handleResult(it.status)
                    )
                }),
                "status" to handleResult(decisionTableResult.status)
            )
        }
    }

    private fun handleScenarioResult(scenarioResult: ScenarioResult): JsonObject {
        return json {
            obj(
                "steps" to array(scenarioResult.steps.map {
                    obj(
                        it.value to handleResult(it.status)
                    )
                }),
                "status" to handleResult(scenarioResult.status)
            )
        }
    }

    private fun handleResult(status: Status): String {
        return when (status) {
            Status.Executed -> "executed"
            is Status.Disabled -> "disabled"
            Status.Manual -> "manual"
            Status.Skipped -> "skipped"
            Status.Unknown -> "unknown"
            is Status.Failed -> "failed"
            is Status.Exception -> "exception"
        }
    }
}
