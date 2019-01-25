package org.livingdoc.engine

import org.livingdoc.engine.execution.examples.scenarios.ScenarioFixtureModel
import org.livingdoc.engine.execution.examples.scenarios.matching.NoMatchingStepTemplate
import org.livingdoc.repositories.model.scenario.Scenario

/**
 * Default matcher to find the right fixture classes for a given list of tables.
 */
class ScenarioToFixtureMatcher {

    fun findMatchingFixture(scenario: Scenario, fixtures: List<Class<*>>): Class<*>? {
        return fixtures.firstOrNull { fixture ->
            val model = ScenarioFixtureModel(fixture)
            try {
                scenario.steps.forEach { (step) ->
                    model.getMatchingStepTemplate(step)
                }
                true
            } catch (e: NoMatchingStepTemplate) {
                false
            }
        }
    }
}
