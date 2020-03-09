package org.livingdoc.jvm.decisiontable

import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureExtensionsInterface
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.results.Status
import org.livingdoc.results.TestDataResult
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import org.livingdoc.results.examples.decisiontables.RowResult

class DecisionTableFixture(
    private val fixtureModel: DecisionTableFixtureModel,
    private val manager: FixtureExtensionsInterface
) : Fixture<DecisionTable> {
    override fun execute(testData: DecisionTable): TestDataResult<DecisionTable> {
        val resultBuilder = DecisionTableResult.Builder()
            .withFixtureSource(fixtureModel.fixtureClass.java)
            .withDecisionTable(testData)

        var exceptionThrown = false

        try {
            manager.onBeforeFixture()
        } catch (e: Throwable) {
            val exception = manager.handleBeforeMethodExecutionException(e)
            if (exception != null) {
                exceptionThrown = true
                resultBuilder.withStatus(Status.Exception(e))
            }
        }

        if (!exceptionThrown) {
            try {
                executeDecisionTable(testData).forEach { resultBuilder.withRow(it) }
            } catch (e: Throwable) {
                val exception = manager.handleTestExecutionException(e)
                if (exception != null) {
                    exceptionThrown = true
                    resultBuilder.withStatus(Status.Exception(e))
                }
            } catch (e: AssertionError) {
                val exception = manager.handleTestExecutionException(e)
                if (exception != null) {
                    exceptionThrown = true
                    resultBuilder.withStatus(Status.Exception(e))
                }
            }
        }

        if (exceptionThrown) {
            try {
                manager.onAfterFixture()
            } catch (e: Exception) {
                manager.handleAfterMethodExecutionException(e)
                resultBuilder.withStatus(Status.Exception(e))
            }
        }

        return resultBuilder.withUnassignedRowsSkipped().build()
    }

    private fun executeDecisionTable(
        testData: DecisionTable
    ): List<RowResult> {

        val inputHeaders = filterHeaders(testData) { (name) -> fixtureModel.isInputAlias(name) }
        val checkHeaders = filterHeaders(testData) { (name) -> fixtureModel.isCheckAlias(name) }

        // TODO Implement parallel execution
        return testData.rows.map { row ->
            RowExecution(
                fixtureModel,
                row,
                inputHeaders,
                checkHeaders
            ).execute()
        }
    }

    private fun filterHeaders(decisionTable: DecisionTable, predicate: (Header) -> Boolean): Set<Header> {
        return decisionTable.headers.filter(predicate).toSet()
    }
}
