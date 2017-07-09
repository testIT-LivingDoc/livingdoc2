package org.livingdoc.engine.execution.examples.decisiontables

import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTable
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult

/**
 * This class handles the execution of [DecisionTable] examples.
 */
class DecisionTableExecutor {

    fun execute(decisionTable: DecisionTable, fixtureClass: Class<*>, document: Any? = null): DecisionTableResult {
        return DecisionTableExecution(fixtureClass, decisionTable, document).execute()
    }

}