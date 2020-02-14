package org.livingdoc.reports.json

import com.beust.klaxon.JsonObject
import com.beust.klaxon.json
import org.livingdoc.config.YamlUtils
import org.livingdoc.reports.ReportWriter
import org.livingdoc.reports.spi.Format
import org.livingdoc.reports.spi.ReportRenderer
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import org.livingdoc.results.examples.scenarios.ScenarioResult
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Format("json")
class JsonReportRenderer : ReportRenderer {

    override fun render(documentResults: List<DocumentResult>, config: Map<String, Any>) {
        val jsonConfig = YamlUtils.toObject(config, JsonReportConfig::class)
        val outputFolder = Paths.get(
            jsonConfig.outputDir,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
        ).toString()
        val reportWriter = ReportWriter(outputFolder, fileExtension = "json")

        documentResults.forEach { documentResult ->
            val json = render(documentResult, jsonConfig.prettyPrinted)
            reportWriter.writeToFile(
                json,
                documentResult.documentClass.name
            )
        }
    }

    /**
     * Create a html string from a [DocumentResult]
     */
    fun render(documentResult: DocumentResult, prettyPrinted: Boolean): String {
        val exampleResults = json {
            obj("results" to array(documentResult.results.map {
                when (it) {
                    is DecisionTableResult -> handleDecisionTableResult(it)
                    is ScenarioResult -> handleScenarioResult(it)
                    else -> throw IllegalArgumentException("Unknown Result type.")
                }
            }))
        }

        return exampleResults.toJsonString(prettyPrinted)
    }

    private fun handleDecisionTableResult(decisionTableResult: DecisionTableResult): JsonObject {
        val title = decisionTableResult.decisionTable.description.name
        val desc = decisionTableResult.decisionTable.description.descriptiveText

        return json {
            obj(
                "title" to title,
                "description" to (if (desc == "") "" else array(desc.split("\n"))),
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
        val title = scenarioResult.scenario.description.name
        val desc = scenarioResult.scenario.description.descriptiveText

        return json {
            obj(
                "title" to title,
                "description" to (if (desc == "") "" else array(desc.split("\n"))),
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
            is Status.ReportActualResult -> "report-actual-result"
            is Status.Failed -> "failed"
            is Status.Exception -> "exception"
        }
    }
}
