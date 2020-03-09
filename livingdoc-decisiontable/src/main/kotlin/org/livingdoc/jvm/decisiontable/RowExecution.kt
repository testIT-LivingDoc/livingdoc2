package org.livingdoc.jvm.decisiontable

import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.results.Status
import org.livingdoc.results.examples.decisiontables.FieldResult
import org.livingdoc.results.examples.decisiontables.RowResult

class RowExecution(
    private val fixtureModel: DecisionTableFixtureModel,
    private val row: Row,
    private val inputHeaders: Set<Header>,
    private val checkHeaders: Set<Header>,
    private val instance: DecisionTableFixtureInstance =
    DecisionTableFixtureInstance.createInstance(fixtureModel.fixtureClass)
) {
    fun execute(): RowResult {
        val rowResultBuilder = RowResult.Builder()
            .withRow(row)

        getHeaderToFieldMapForRow(row, inputHeaders).forEach { (header, field) ->
            val fieldResult = setInput(header, field)
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

    private fun setInput(header: Header, field: Field): FieldResult {
        val inputSetter = fixtureModel.getInput(header.name) ?: error("")
        inputSetter.call(instance.instance, field.value)

        return FieldResult.Builder().withValue(field.value).withStatus(Status.Executed).build()
    }
}
