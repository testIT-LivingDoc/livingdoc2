package org.livingdoc.engine.execution.examples.scenarios

import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.repositories.model.scenario.Scenario

class ScenarioFixtureWrapper(
    val fixtureClass: Class<*>
) : Fixture<Scenario> {
    /**
     * Executes the wrapped fixture class with the give scenario
     *
     * @param testData A scenario instance that can be mapped to the wrapped fixture
     * @return A ScenarioResult for the execution
     */
    override fun execute(testData: Scenario): ScenarioResult {
        return ScenarioExecution(this.fixtureClass, testData, null).execute()
    }
}
