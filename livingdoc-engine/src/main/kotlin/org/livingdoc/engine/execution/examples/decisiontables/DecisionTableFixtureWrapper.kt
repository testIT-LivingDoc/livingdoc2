package org.livingdoc.engine.execution.examples.decisiontables

import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.repositories.model.decisiontable.DecisionTable

class DecisionTableFixtureWrapper(
    val fixtureClass: Class<*>
) : Fixture<DecisionTable> {
    /**
     * Executes the wrapped fixture class with the give decision table
     *
     * @param testData A decision table instance that can be mapped to the wrapped fixture
     * @return A DecisionTableResult for the execution
     */
    override fun execute(testData: DecisionTable): DecisionTableResult {
        return DecisionTableExecution(this.fixtureClass, testData, null).execute()
    }
}
