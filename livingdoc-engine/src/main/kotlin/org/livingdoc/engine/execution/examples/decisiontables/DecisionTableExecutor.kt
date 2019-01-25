package org.livingdoc.engine.execution.examples.decisiontables

import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.repositories.model.decisiontable.DecisionTable

/**
 * This class handles the execution of [DecisionTable] examples.
 */
class DecisionTableExecutor {

    fun execute(decisionTable: DecisionTable, fixtureClass: Class<*>, document: Any? = null): DecisionTableResult {
        return DecisionTableExecution(fixtureClass, decisionTable, document).execute()
    }
}