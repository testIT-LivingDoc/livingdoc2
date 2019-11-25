package org.livingdoc.engine.execution.examples.decisiontables

import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.repositories.model.decisiontable.DecisionTable

internal class DecisionTableNoFixtureExecution(
    private val decisionTable: DecisionTable,
    document: Any?
) {
    /**
     * Executes the configured [DecisionTable] without a [DecisionTableFixtureModel].
     *
     * Does not throw any kind of exception.
     * Exceptional state of the execution is packaged inside the [DecisionTableResult] in
     * the form of different status objects.
     */
    fun execute(): DecisionTableResult {
        val result = DecisionTableResult.from(decisionTable)

        if (decisionTable.description.isManual) {
            result.status = Status.Manual
        }

        return result
    }
}
