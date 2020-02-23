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
        return ScenarioResult.Builder().withFixtureSource(context.fixtureClass.java).withScenario(testData)
            .withStatus(Status.Unknown).withUnassignedSkipped().build()
    }
}
