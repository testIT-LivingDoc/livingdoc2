package org.livingdoc.jvm.decisiontable

import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureExtensionsInterface
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.results.Status
import org.livingdoc.results.TestDataResult
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import org.livingdoc.results.examples.decisiontables.RowResult

class DecisionTableFixture(
    val context: FixtureContext,
    private val manager: FixtureExtensionsInterface,
    private val fixtureModel: DecisionTableFixtureModel
) : Fixture<DecisionTable> {
    override fun execute(testData: DecisionTable): TestDataResult<DecisionTable> {
        val resultBuilder = DecisionTableResult.Builder()
            .withFixtureSource(context.fixtureClass.java)
            .withDecisionTable(testData)

        try {
            assertFixtureIsDefinedCorrectly(testData)
        } catch (e: Exception) {
            resultBuilder.withStatus(Status.Exception(e))
            return resultBuilder.withUnassignedRowsSkipped().build()
        }

        try {
            try {
                manager.onBeforeFixture()
            } catch (e: Exception) {
                manager.handleBeforeMethodExecutionException(e)
                resultBuilder.withStatus(Status.Exception(e))
            }

            try {
                executeDecisionTable(testData, resultBuilder).forEach { resultBuilder.withRow(it) }
            } catch (e: Exception) {
                manager.handleTestExecutionException(e)
                resultBuilder.withStatus(Status.Exception(e))
            } catch (e: AssertionError) {
                manager.handleTestExecutionException(e)
                resultBuilder.withStatus(Status.Exception(e))
            }
        } finally {
            executeAfter(resultBuilder)
        }

        return resultBuilder.withUnassignedRowsSkipped().build()
    }

    private fun assertFixtureIsDefinedCorrectly(decisionTable: DecisionTable) {
        val errors = DecisionTableFixtureChecker.check(fixtureModel)
        if (errors.isNotEmpty()) {
            throw MalformedDecisionTableFixtureException(context.fixtureClass.java, errors)
        }

        val unmappedHeaders = findUnmappedHeaders(decisionTable)
        if (unmappedHeaders.isNotEmpty()) {
            throw UnmappedHeaderException(context.fixtureClass.java, unmappedHeaders)
        }
    }

    private fun filterHeaders(decisionTable: DecisionTable, predicate: (Header) -> Boolean): Set<Header> {
        return decisionTable.headers.filter(predicate).toSet()
    }

    private fun executeDecisionTable(testData: DecisionTable, resultBuilder: DecisionTableResult.Builder): List<RowResult> {

        val inputHeaders = filterHeaders(testData) { (name) -> fixtureModel.isInputAlias(name) }
        val checkHeaders = filterHeaders(testData) { (name) -> fixtureModel.isCheckAlias(name) }

        // TODO Implement parallel execution
        return testData.rows.map { row -> RowExecution(context, fixtureModel, row, inputHeaders, checkHeaders).execute() }
    }

    private fun findUnmappedHeaders(decisionTable: DecisionTable): List<String> {
        return decisionTable.headers
            .filter { (name) -> !fixtureModel.isInputAlias(name) && !fixtureModel.isCheckAlias(name) }
            .map { it.name }
    }

    private fun executeAfter(resultBuilder: DecisionTableResult.Builder) {
        try {
            manager.onAfterFixture()
        } catch (e: Exception) {
            manager.handleAfterMethodExecutionException(e)
            resultBuilder.withStatus(Status.Exception(e))
        }
    }

    internal class MalformedDecisionTableFixtureException(fixtureClass: Class<*>, errors: List<String>) :
        RuntimeException(
            "The fixture class <$fixtureClass> is malformed: \n${errors.joinToString(
                separator = "\n",
                prefix = "  - "
            )}"
        )

    internal class UnmappedHeaderException(fixtureClass: Class<*>, unmappedHeaders: List<String>) : RuntimeException(
        "The fixture class <$fixtureClass> has no methods for the following columns: \n${unmappedHeaders.joinToString(
            separator = "\n",
            prefix = "  - "
        )}"
    )
}
