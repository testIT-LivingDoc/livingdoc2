package org.livingdoc.engine.execution.examples.decisiontables.model

import org.livingdoc.engine.execution.Result

data class DecisionTableResult(
        val headers: List<Header>,
        val rows: List<RowResult>,
        var result: Result = Result.Unknown
) {

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
                    columnToFieldCopy.put(Header(name), FieldResult(value))
                }
                rowCopies.add(RowResult(columnToFieldCopy))
            }
            return DecisionTableResult(headerCopies, rowCopies)
        }
    }

}