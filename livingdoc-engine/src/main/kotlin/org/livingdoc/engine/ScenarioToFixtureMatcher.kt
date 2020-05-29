package org.livingdoc.engine

import org.livingdoc.engine.execution.examples.scenarios.ScenarioFixtureModel
import org.livingdoc.engine.execution.examples.scenarios.ScenarioFixtureWrapper
import org.livingdoc.engine.execution.examples.scenarios.ScenarioNoFixture
import org.livingdoc.engine.execution.examples.scenarios.matching.NoMatchingStepTemplate
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.repositories.model.scenario.Scenario

/**
 * Default matcher to find the right fixture classes for a given list of tables.
 */
class ScenarioToFixtureMatcher {

    fun findMatchingFixture(scenario: Scenario, fixtures: List<ScenarioFixtureWrapper>): Fixture<Scenario> {
        if (scenario.description.isManual) {
            return ScenarioNoFixture()
        }

        return fixtures.firstOrNull { fixture ->
            val model = ScenarioFixtureModel(fixture.fixtureClass)
            try {
                scenario.steps.forEach { (step) ->
                    model.getMatchingStepTemplate(step)
                }
                true
            } catch (e: NoMatchingStepTemplate) {
                false
            }
        } ?: throw ScenarioFixtureNotFoundException(scenario, fixtures)
    }

    class ScenarioFixtureNotFoundException(scenario: Scenario, fixtures: List<ScenarioFixtureWrapper>) :
        RuntimeException("Could not find Fixture for Scenario:\n$scenario\nAvailable fixtures:\n$fixtures")
}
