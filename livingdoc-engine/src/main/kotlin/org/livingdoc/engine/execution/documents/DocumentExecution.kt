package org.livingdoc.engine.execution.documents

import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.engine.DecisionTableToFixtureMatcher
import org.livingdoc.engine.ScenarioToFixtureMatcher
import org.livingdoc.engine.execution.MalformedFixtureException
import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.fixtures.FixtureMethodInvoker
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.scenario.Scenario

/**
 * A DocumentExecution represents a single execution of a [DocumentFixture].
 *
 * @see DocumentFixture
 */
internal class DocumentExecution(
    private val documentClass: Class<*>,
    private val document: Document,
    private val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher,
    private val scenarioToFixtureMatcher: ScenarioToFixtureMatcher
) {
    private val documentFixtureModel: DocumentFixtureModel = DocumentFixtureModel(documentClass)
    private val builder = DocumentResult.Builder().withDocumentClass(documentClass)
    private val methodInvoker: FixtureMethodInvoker = FixtureMethodInvoker(documentClass)

    /**
     * Execute performs the actual execution
     *
     * @return a [DocumentResult] describing the outcome of this DocumentExecution
     */
    fun execute(): DocumentResult {
        try {
            assertFixtureIsDefinedCorrectly()
            executeBeforeMethods()
            executeFixtures()
            executeAfterMethods()
            builder.withStatus(Status.Executed)
        } catch (e: MalformedFixtureException) {
            builder.withStatus(Status.Exception(e))
        }

        return builder.build()
    }

    /**
     * assertFixtureIsDefinedCorrectly checks that the [DocumentFixture] is defined correctly
     */
    private fun assertFixtureIsDefinedCorrectly() {
        val errors = DocumentFixtureChecker.check(documentFixtureModel)

        if (errors.isNotEmpty()) {
            throw MalformedFixtureException(documentClass, errors)
        }
    }

    /**
     * ExecuteBeforeMethods invokes all [Before] methods on the [DocumentFixture].
     *
     * @see Before
     * @see DocumentFixture
     */
    private fun executeBeforeMethods() {
        documentFixtureModel.beforeMethods.forEach { method -> methodInvoker.invokeStatic(method) }
    }

    /**
     * ExecuteFixtures runs all examples contained in the document with their corresponding fixture.
     */
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

    /**
     * ExecuteAfterMethods invokes all [After] methods on the [DocumentFixture].
     *
     * @see After
     * @see DocumentFixture
     */
    private fun executeAfterMethods() {
        documentFixtureModel.afterMethods.forEach { method -> methodInvoker.invokeStatic(method) }
    }
}
