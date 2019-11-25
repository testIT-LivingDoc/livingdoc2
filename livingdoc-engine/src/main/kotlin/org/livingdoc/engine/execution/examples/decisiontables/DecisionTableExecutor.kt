package org.livingdoc.engine.execution.examples.decisiontables

import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.repositories.model.decisiontable.DecisionTable

/**
 * This class handles the execution of [DecisionTable] examples.
 */
class DecisionTableExecutor {

    /**
     * Executes the given [DecisionTable] with the given fixture
     */
    fun execute(decisionTable: DecisionTable, fixtureClass: Class<*>, document: Any? = null): DecisionTableResult {
        return DecisionTableExecution(fixtureClass, decisionTable, document).execute()
    }

    /**
     * Executes the given [DecisionTable] without a fixture
     */
    fun executeNoFixture(decisionTable: DecisionTable, document: Any? = null): DecisionTableResult {
        return DecisionTableNoFixtureExecution(decisionTable, document).execute()
    }
}
