package org.livingdoc.jvm.decisiontable

import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureExtensionsInterface
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.results.Status
import org.livingdoc.results.TestDataResult
import org.livingdoc.results.examples.decisiontables.DecisionTableResult

class DecisionTableFixture(
    val context: FixtureContext,
    val manager: FixtureExtensionsInterface
) : Fixture<DecisionTable> {
    override fun execute(testData: DecisionTable): TestDataResult<DecisionTable> {
        return DecisionTableResult.Builder().withFixtureSource(context.fixtureClass.java).withDecisionTable(testData)
            .withStatus(Status.Unknown).withUnassignedRowsSkipped().build()
    }
}
