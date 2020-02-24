package org.livingdoc.jvm.scenario

import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureExtensionsInterface
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.results.Status
import org.livingdoc.results.TestDataResult
import org.livingdoc.results.examples.scenarios.ScenarioResult

class ScenarioFixture(
    val context: FixtureContext,
    private val manager: FixtureExtensionsInterface
) : Fixture<Scenario> {
    override fun execute(testData: Scenario): TestDataResult<Scenario> {
        var scResult: ScenarioResult

        if (manager.shouldExecute().disabled)
            return if (!manager.shouldExecute().reason.isNullOrEmpty())
                ScenarioResult.Builder().withFixtureSource(context.fixtureClass.java).withScenario(testData)
                    .withStatus(Status.Disabled(manager.shouldExecute().reason!!)).withUnassignedSkipped().build()
            else ScenarioResult.Builder().withFixtureSource(context.fixtureClass.java).withScenario(testData)
                .withStatus(Status.Disabled()).withUnassignedSkipped().build()

        // TODO use assert() and add it to withStatus(Status.Failed(assertFailure))
        try {
            manager.onBeforeFixture()
            scResult = ScenarioResult.Builder().withFixtureSource(context.fixtureClass.java).withScenario(testData)
                .withStatus(Status.Executed).withUnassignedSkipped().build()
            manager.onAfterFixture()
        } catch (throwable: IllegalStateException) {
            manager.handleTestExecutionException(throwable)
            scResult = ScenarioResult.Builder().withFixtureSource(context.fixtureClass.java).withScenario(testData)
                .withStatus(Status.Failed()).withUnassignedSkipped().build()
        }

        return scResult
    }
}
