package org.livingdoc.engine.execution.examples.scenarios

import org.livingdoc.engine.execution.examples.TestDataResult
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.repositories.model.TestData
import org.livingdoc.repositories.model.scenario.Scenario

class ScenarioFixtureWrapper(
    val fixtureClass: Class<*>
) : Fixture {

    /**
     * Executes the wrapped fixture class with the give scenario
     *
     * @param testData A scenario instance that can be mapped to the wrapped fixture
     * @return A ScenarioResult for the execution
     */
    fun execute(testData: Scenario): ScenarioResult {
        return ScenarioExecution(this.fixtureClass, testData, null).execute()
    }

    override fun execute(testData: TestData): TestDataResult {
        return when(testData) {
            is Scenario -> execute(testData)
            else -> throw TestDataNoScenarioException(testData, this.fixtureClass)
        }
    }


    class TestDataNoScenarioException(testData: TestData, fixtureClass: Class<*>) :
        RuntimeException(
            "The given TestData is a ${testData.javaClass.canonicalName}" +
                    " and not a Scenario and can not be used to execute this fixture: $fixtureClass"
        )
}
