package org.livingdoc.engine.execution.examples.scenarios

import org.livingdoc.engine.LivingDoc
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.results.Status
import org.livingdoc.results.examples.scenarios.ScenarioResult

class ScenarioNoFixture : Fixture<Scenario> {
    /**
     * Executes the given test data as a manual test
     *
     * Does not throw any kind of exception.
     * Exceptional state of the execution is packaged inside the [ScenarioResult] in
     * the form of different status objects.
     *
     * @param testData Test data of the corresponding type
     */
    override fun execute(testData: Scenario): ScenarioResult {
        val resultBuilder = ScenarioResult.Builder()
            .withScenario(testData).withFixtureSource(this.javaClass)

        if (LivingDoc.failFastActivated) {
            return resultBuilder.withStatus(
                Status.Skipped
            ).build()
        }

        if (testData.description.isManual) {
            resultBuilder.withStatus(Status.Manual)
        }

        return resultBuilder.build()
    }
}
