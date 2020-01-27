package org.livingdoc.repositories.format

import io.cucumber.gherkin.GherkinDocumentBuilder
import io.cucumber.gherkin.Parser
import io.cucumber.messages.IdGenerator
import io.cucumber.messages.Messages
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.DocumentFormat
import org.livingdoc.repositories.model.TestDataDescription
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.repositories.model.scenario.Step
import java.io.InputStream

/**
 * GherkinFormat supports parsing [Documents][Document] described using Gherkin.
 */
class GherkinFormat : DocumentFormat {
    private val id = IdGenerator.Incrementing()

    override fun canHandle(fileExtension: String): Boolean {
        return fileExtension == "feature"
    }

    override fun parse(stream: InputStream): Document {
        val gherkin = Parser(GherkinDocumentBuilder(id)).parse(stream.reader())

        return Document(with(gherkin.feature) {
            childrenList.mapNotNull {
                when (it.valueCase) {
                    Messages.GherkinDocument.Feature.FeatureChild.ValueCase.SCENARIO -> it.scenario
                    else -> null
                }
            }.map { scenario ->
                val steps = scenario.stepsList.map { step ->
                    Step(step.text)
                }

                Scenario(steps, TestDataDescription(scenario.name, false, scenario.description.trim()))
            }
        })
    }
}
