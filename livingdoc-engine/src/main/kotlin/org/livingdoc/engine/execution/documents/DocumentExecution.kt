package org.livingdoc.engine.execution.documents

import org.livingdoc.engine.DecisionTableToFixtureMatcher
import org.livingdoc.engine.ScenarioToFixtureMatcher
import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.fixtures.FixtureMethodInvoker
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.scenario.Scenario

internal class DocumentExecution(
    private val documentClass: Class<*>,
    private val document: Document,
    private val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher,
    private val scenarioToFixtureMatcher: ScenarioToFixtureMatcher
) {
    private val documentFixtureModel: DocumentFixtureModel = DocumentFixtureModel(documentClass)
    private val builder = DocumentResult.Builder().withDocumentClass(documentClass).withStatus(Status.Executed)
    private val methodInvoker: FixtureMethodInvoker = FixtureMethodInvoker(documentClass)
    private val fixture: Any = documentClass.getDeclaredConstructor().newInstance()

    fun execute(): DocumentResult {
        executeBeforeMethods()
        executeFixtures()
        executeAfterMethods()
        return builder.build()
    }

    private fun executeBeforeMethods() {
        documentFixtureModel.beforeMethods.forEach { method -> methodInvoker.invoke(method, fixture) }
    }

    private fun executeFixtures() {
        document.elements.mapNotNull { element ->
            when (element) {
                is DecisionTable -> {
                    decisionTableToFixtureMatcher
                            .findMatchingFixture(element, documentFixtureModel.decisionTableFixtures)
                            .execute(element)
                }
                is Scenario -> {
                    scenarioToFixtureMatcher
                            .findMatchingFixture(element, documentFixtureModel.scenarioFixtures)
                            .execute(element)
                }
                else -> null
            }
        }.forEach { result -> builder.withResult(result) }
    }

    private fun executeAfterMethods() {
        documentFixtureModel.afterMethods.forEach { method -> methodInvoker.invoke(method, fixture) }
    }
}
