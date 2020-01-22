package org.livingdoc.engine

import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableFixtureModel
import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableFixtureWrapper
import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableNoFixture
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.repositories.model.decisiontable.DecisionTable

/**
 * Default matcher to find the right fixture classes in a given list of FixtureWrappers.
 */
class DecisionTableToFixtureMatcher {

    /**
     * This function returns the matching fixture for a Decision Table
     * @param decisionTable The table for which a matching fixture is needed
     * @param fixtures A list of fixture wrappers that is searched through
     * @return the matching Fixture
     */
    fun findMatchingFixture(
        decisionTable: DecisionTable,
        fixtures: List<DecisionTableFixtureWrapper>
    ): Fixture<DecisionTable> {
        if (decisionTable.description.isManual) {
            return DecisionTableNoFixture()
        }

        val headerNames = decisionTable.headers.map { it.name }
        val numberOfHeaders = headerNames.size

        val matchingFixtures = fixtures.filter { fixture ->
            val fixtureModel = DecisionTableFixtureModel(fixture.fixtureClass)
            val aliases = fixtureModel.aliases
            val numberOfAliases = aliases.size
            val numberOfMatchedHeaders = headerNames.filter { aliases.contains(it) }.size

            numberOfMatchedHeaders == numberOfHeaders && numberOfMatchedHeaders == numberOfAliases
        }

        if (matchingFixtures.size > 1) {
            throw MultipleMatchingFixturesException(headerNames, matchingFixtures)
        }
        return matchingFixtures.firstOrNull() ?: throw NoMatchingFixturesException(headerNames, fixtures)
    }

    /**
     * This exception is thrown whenever there are more than one matching fixture for a Decision Table
     */
    class MultipleMatchingFixturesException(
        headerNames: List<String>,
        matchingFixtures: List<DecisionTableFixtureWrapper>
    ) : RuntimeException("Could not identify a unique fixture matching the Decision Table's headers " +
            "${headerNames.map { "'$it'" }}. Matching fixtures found: $matchingFixtures"
    )

    /**
     * This exception is thrown whenever there is no matching fixture for a Decision Table
     */
    class NoMatchingFixturesException(headerNames: List<String>, fixtures: List<DecisionTableFixtureWrapper>) :
        RuntimeException("Could not find any fixture matching the Decision Table's headers " +
                "${headerNames.map { "'$it'" }}. Available fixtures: $fixtures"
        )
}
