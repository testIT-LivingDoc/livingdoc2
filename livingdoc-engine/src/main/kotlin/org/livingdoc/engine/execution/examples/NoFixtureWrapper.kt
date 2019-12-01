package org.livingdoc.engine.execution.examples

import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableNoFixtureExecution
import org.livingdoc.engine.execution.examples.scenarios.ScenarioNoFixtureExecution
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.repositories.model.TestData
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.scenario.Scenario

/**
 * Handles execution of test data without a fixture
 *
 * @param T Specifies the type of the executed test data
 */
class NoFixtureWrapper<T : TestData> : Fixture<T> {
    /**
     * Exetutes the given test data as a manual test
     *
     * @param testData Test data of the corresponding type
     * @throws TestDataUnknownTypeException when the given test data can't be executed by this class
     */
    override fun execute(testData: T): TestDataResult {
        return when (testData) {
            is DecisionTable -> {
                DecisionTableNoFixtureExecution(testData, null).execute()
            }
            is Scenario -> {
                ScenarioNoFixtureExecution(testData, null).execute()
            }
            else -> throw TestDataUnknownTypeException(testData)
        }
    }

    class TestDataUnknownTypeException(testData: TestData) :
        RuntimeException(
            "The given TestData is a ${testData.javaClass.canonicalName} and can not be executed with this wrapper."
        )
}
