package org.livingdoc.engine.execution.examples.scenarios

import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.repositories.model.scenario.Scenario

/**
 * This class handles the execution of [Scenario] examples.
 */
class ScenarioExecutor {

    /**
     * Executes the given [Scenario] with the given fixture
     */
    fun execute(scenario: Scenario, fixtureClass: Class<*>, document: Any? = null): ScenarioResult {
        return ScenarioExecution(fixtureClass, scenario, document).execute()
    }

    /**
     * Executes the given [Scenario] without a fixture
     */
    fun executeNoFixture(scenario: Scenario, document: Any? = null): ScenarioResult {
        return ScenarioNoFixtureExecution(scenario, document).execute()
    }
}
