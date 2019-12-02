package org.livingdoc.engine.execution.examples.scenarios

import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.repositories.model.scenario.Scenario

internal class ScenarioNoFixtureExecution(
    private val scenario: Scenario,
    document: Any?
) {
    /**
     * Executes the configured [Scenario] without a [ScenarioFixtureModel].
     *
     * Does not throw any kind of exception.
     * Exceptional state of the execution is packaged inside the [ScenarioResult] in
     * the form of different status objects.
     */
    fun execute(): ScenarioResult {
        val resultBuilder = ScenarioResult.Builder()
            .ofScenario(scenario)

        if (scenario.description.isManual) {
            resultBuilder.withStatus(Status.Manual)
        }

        return resultBuilder.build()
    }
}
