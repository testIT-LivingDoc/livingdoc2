package org.livingdoc.engine.execution.examples.decisiontables.model

import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.TestDataResult
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Header

data class DecisionTableResult(
    val headers: List<Header>,
    val rows: List<RowResult>,
    var status: Status = Status.Unknown
) : TestDataResult {

    companion object {
        fun from(decisionTable: DecisionTable): DecisionTableResult {
            val headerCopies = mutableListOf<Header>()
            decisionTable.headers.forEach { (name) ->
                headerCopies.add(Header(name))
            }
            val rowCopies = mutableListOf<RowResult>()
            decisionTable.rows.forEach { (columnToField) ->
                val columnToFieldCopy = mutableMapOf<Header, FieldResult>()
                columnToField.forEach { (name), (value) ->
                    columnToFieldCopy[Header(name)] = FieldResult(value)
                }
                rowCopies.add(RowResult(columnToFieldCopy))
            }
            return DecisionTableResult(headerCopies, rowCopies)
        }
    }
}
