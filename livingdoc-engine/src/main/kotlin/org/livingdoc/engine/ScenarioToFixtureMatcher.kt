package org.livingdoc.engine

import org.livingdoc.repositories.model.scenario.Scenario

/**
 * Default matcher to find the right fixture classes for a given list of tables.
 */
class ScenarioToFixtureMatcher {

    fun findMatchingFixture(scenario: Scenario, fixtures: List<Class<*>>): Class<*>? {
        return null
    }

}
