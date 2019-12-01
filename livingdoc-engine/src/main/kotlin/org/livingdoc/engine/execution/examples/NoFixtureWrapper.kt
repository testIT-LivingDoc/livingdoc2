package org.livingdoc.engine.execution.examples

import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableNoFixtureExecution
import org.livingdoc.engine.execution.examples.scenarios.ScenarioNoFixtureExecution
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.repositories.model.TestData
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.scenario.Scenario

class NoFixtureWrapper : Fixture {
    override fun execute(testData: TestData): TestDataResult {
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
