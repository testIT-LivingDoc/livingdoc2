package org.livingdoc.jvm.decisiontable

import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureManager
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.results.Status
import org.livingdoc.results.TestDataResult
import org.livingdoc.results.examples.decisiontables.DecisionTableResult

class DecisionTableFixture(
    private val fixtureModel: DecisionTableFixtureModel,
    private val manager: FixtureManager
) : Fixture<DecisionTable> {
    override fun execute(testData: DecisionTable): TestDataResult<DecisionTable> {
        val resultBuilder = DecisionTableResult.Builder()
            .withFixtureSource(fixtureModel.fixtureClass.java)
            .withDecisionTable(testData)

        if (onBeforeCallback(resultBuilder)) {
            executeDecisionTable(testData, resultBuilder)
        }
        if (onAfterCallback(resultBuilder)) {
            resultBuilder.withStatus(Status.Executed)
        }
        return resultBuilder.withUnassignedRowsSkipped().build()
    }

    private fun executeDecisionTable(
        testData: DecisionTable,
        resultBuilder: DecisionTableResult.Builder
    ): Boolean {
        var exceptionThrown = false
        val inputHeaders = filterHeaders(testData) { (name) -> fixtureModel.isInputAlias(name) }
        val checkHeaders = filterHeaders(testData) { (name) -> fixtureModel.isCheckAlias(name) }

        // TODO Implement parallel execution
        testData.rows.forEach { row ->
            try {
                val result = RowExecution(
                    fixtureModel,
                    row,
                    inputHeaders,
                    checkHeaders
                ).execute()
                resultBuilder.withRow(result)
            } catch (@Suppress("TooGenericExceptionCaught") e: Throwable) {
                val exception = manager.handleTestExecutionException(e)
                if (exception != null) {
                    exceptionThrown = true
                    resultBuilder.withStatus(Status.Exception(e))
                }
            }
        }
        return exceptionThrown
    }

    private fun filterHeaders(decisionTable: DecisionTable, predicate: (Header) -> Boolean): Set<Header> {
        return decisionTable.headers.filter(predicate).toSet()
    }

    private fun onBeforeCallback(resultBuilder: DecisionTableResult.Builder): Boolean {
        return try {
            manager.onBeforeFixture()
            true
        } catch (@Suppress("TooGenericExceptionCaught") e: Throwable) {
            val exception = manager.handleBeforeMethodExecutionException(e)
            if (exception != null) {
                resultBuilder.withStatus(Status.Exception(e))
                false
            } else {
                true
            }
        }
    }

    private fun onAfterCallback(resultBuilder: DecisionTableResult.Builder): Boolean {
        return try {
            manager.onAfterFixture()
            true
        } catch (@Suppress("TooGenericExceptionCaught") e: Throwable) {
            val processedThrowable = manager.handleAfterMethodExecutionException(e)
            if (processedThrowable != null) {
                resultBuilder.withStatus(Status.Exception(e))
                false
            } else {
                true
            }
        }
    }
}
