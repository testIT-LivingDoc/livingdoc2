package org.livingdoc.repositories.format

import com.beust.klaxon.Klaxon
import io.cucumber.gherkin.GherkinDocumentBuilder
import io.cucumber.gherkin.Parser
import io.cucumber.gherkin.pickles.PickleCompiler
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

        val descriptionMap = gherkin.feature.childrenList.mapNotNull {
            when (it.valueCase) {
                Messages.GherkinDocument.Feature.FeatureChild.ValueCase.SCENARIO ->
                    (it.scenario.id to it.scenario.description)
                else -> null
            }
        }.toMap()

        val pickles = PickleCompiler(id).compile(gherkin.build(), gherkin.uri)

        return Document(pickles.map { pickle ->
            val steps = pickle.stepsList.map { step ->

                val argument = when (step.argument.messageCase) {
                    Messages.PickleStepArgument.MessageCase.DOC_STRING -> step.argument.docString.content
                    Messages.PickleStepArgument.MessageCase.DATA_TABLE -> Klaxon().toJsonString(DataTable(
                        rows = step.argument.dataTable.rowsList.map {
                            it.cellsList.map {
                                it.value
                            }
                        }))
                    else -> ""
                }

                Step((step.text + " " + argument).trim())
            }

            val id = pickle.getAstNodeIds(0)
            val scenarioDescription = gherkin.feature.description + "\n\n" + descriptionMap[id]

            Scenario(
                steps,
                TestDataDescription(pickle.name, false, scenarioDescription.trimIndent().trim())
            )
        })
    }
}
