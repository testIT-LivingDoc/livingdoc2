package org.livingdoc.engine.execution.documents

import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.engine.DecisionTableToFixtureMatcher
import org.livingdoc.engine.LivingDoc
import org.livingdoc.engine.ScenarioToFixtureMatcher
import org.livingdoc.engine.execution.MalformedFixtureException
import org.livingdoc.engine.execution.groups.GroupFixtureModel
import org.livingdoc.engine.fixtures.FixtureMethodInvoker
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.model.TestData
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.results.Status
import org.livingdoc.results.TestDataResult
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import org.livingdoc.results.examples.scenarios.ScenarioResult
import java.time.Duration
import kotlin.system.measureTimeMillis

/**
 * A DocumentExecution represents a single execution of a [DocumentFixture].
 *
 * @see DocumentFixture
 */
internal class DocumentExecution(
    private val documentClass: Class<*>,
    private val document: Document,
    private val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher,
    private val scenarioToFixtureMatcher: ScenarioToFixtureMatcher,
    private val groupFixtureModel: GroupFixtureModel
) {
    private val documentFixtureModel: DocumentFixtureModel = DocumentFixtureModel(documentClass)
    private val builder = DocumentResult.Builder().withDocumentClass(documentClass).withTags(documentFixtureModel.tags)
    private val methodInvoker: FixtureMethodInvoker = FixtureMethodInvoker(documentClass)

    /**
     * Execute performs the actual execution
     *
     * @return a [DocumentResult] describing the outcome of this DocumentExecution
     */
    fun execute(): DocumentResult {
        if (LivingDoc.failFastActivated) {
            return builder.withStatus(
                Status.Skipped
            ).build()
        }

        val time = Duration.ofMillis(measureTimeMillis {
            try {
                assertFixtureIsDefinedCorrectly()
                executeBeforeMethods()
                executeFixtures()
                executeAfterMethods()
                builder.withStatus(Status.Executed)
            } catch (e: MalformedFixtureException) {
                builder.withStatus(Status.Exception(e))
            }
        })
        builder.withTime(time)

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
        document.elements.map { element ->
            executionFunction(element)
        }.forEach { result -> builder.withResult(result) }
    }

    private fun executionFunction(element: TestData): TestDataResult<TestData> {
        val result = when (element) {
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
            else -> throw IllegalArgumentException()
        }
        when (result) {
            is DecisionTableResult -> {
                checkStatusExceptionForFastFail(result.status)
                result.rows.forEach { rowResult ->
                    checkStatusExceptionForFastFail(rowResult.status)
                    rowResult.headerToField.forEach { (_, fieldResult) ->
                        checkStatusExceptionForFastFail(fieldResult.status)
                    }
                }
            }
            is ScenarioResult -> {
                checkStatusExceptionForFastFail(result.status)
                result.steps.forEach { stepResult ->
                    checkStatusExceptionForFastFail(stepResult.status)
                }
            }
        }

        return result
    }

    private fun checkStatusExceptionForFastFail(status: Status) {
        if (LivingDoc.failFastActivated) {
            return
        }

        val exception = if (status is Status.Exception) status.exception else return

        if (exception is FixtureMethodInvoker.FixtureMethodInvocationException) {
            val occurredException = exception.cause.javaClass
            val failFastExceptions = groupFixtureModel.failFastExceptions + documentFixtureModel.failFastExceptions

            LivingDoc.failFastActivated = (failFastExceptions.any { failFastException ->
                failFastException.isAssignableFrom(occurredException)
            })
        }
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
