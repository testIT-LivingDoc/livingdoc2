package org.livingdoc.jvm.decisiontable

import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.results.examples.decisiontables.RowResult

class RowExecution(
    private val fixtureModel: DecisionTableFixtureModel,
    private val row: Row,
    private val inputHeaders: Set<Header>,
    private val checkHeaders: Set<Header>
) {
    fun execute(): RowResult {
        val rowResultBuilder = RowResult.Builder()
            .withRow(row)

        getHeaderToFieldMapForRow(row, inputHeaders).forEach { (header, field) ->
            val fieldResult = Input(header, field).setInput()
            rowResultBuilder.withFieldResult(header, fieldResult)
        }

        getHeaderToFieldMapForRow(row, checkHeaders).forEach { (header, field) ->
            val checkMethod = fixtureModel.getCheckMethod(header.name)!!
            val fieldResult = CheckExecution(checkMethod, header, field).execute()
            rowResultBuilder.withFieldResult(header, fieldResult)
        }

        return rowResultBuilder.withUnassignedFieldsSkipped().build()
    }

    private fun getHeaderToFieldMapForRow(row: Row, headers: Set<Header>): Map<Header, Field> {
        return row.headerToField
            .filterKeys { headers.contains(it) }
    }
}
