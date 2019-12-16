package org.livingdoc.engine.execution.examples.decisiontables

import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.execution.examples.decisiontables.model.FieldResult
import org.livingdoc.engine.execution.examples.decisiontables.model.RowResult
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
        val result = DecisionTableResult.Builder().withDecisionTable(decisionTable)

        if (decisionTable.description.isManual) {
            result.withStatus(Status.Manual)

            decisionTable.rows.forEach {
                val rowResult = RowResult.Builder()
                    .withRow(it)
                    .withStatus(Status.Manual)

                decisionTable.headers.forEach {
                    rowResult.withFieldResult(
                        it,
                        FieldResult.Builder()
                            .withValue(it.name)
                            .withStatus(Status.Manual)
                            .build()
                    )
                }

                result.withRow(rowResult.build())
            }
        }

        return result.build()
    }
}
