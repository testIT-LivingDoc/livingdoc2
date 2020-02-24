package org.livingdoc.jvm.decisiontable

import org.livingdoc.api.exception.ExampleSyntax
import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.results.Status
import org.livingdoc.results.examples.decisiontables.FieldResult
import org.livingdoc.results.examples.decisiontables.RowResult
import kotlin.reflect.KCallable

class RowExecution(
    val context: FixtureContext,
    private val fixtureModel: DecisionTableFixtureModel,
    private val row: Row,
    val inputHeaders: Set<Header>,
    val checkHeaders: Set<Header>
) {
    private val fieldInjector = FixtureFieldInjector(null)
    private val methodInvoker = FixtureMethodInvoker(null)

    fun execute(): RowResult {
        val rowResultBuilder = RowResult.Builder()
            .withRow(row)

        try {
            invokeBeforeRowMethods(context.fixtureClass.java)

            // TOdo execute row


        } catch (e: Exception) {

        } finally {
            invokeAfterRowMethods(context.fixtureClass.java)
        }

        return rowResultBuilder.withUnassignedFieldsSkipped().build()
    }

    private fun invokeBeforeRowMethods(fixture: Any) {
        fixtureModel.beforeRowMethods.forEach { methodInvoker.invoke(it, fixture) }
    }

    private fun executeRow(rowBuilder: RowResult.Builder) {
        var allInputsSucceeded = true
        val fieldResults = row.headerToField.mapValues {
            FieldResult.Builder().withValue(it.value.value)
        }

        filter(row, inputHeaders).forEach { (inputColumn, field) ->
            val fieldResult = fieldResults[inputColumn] ?: throw IllegalStateException() // This should never happen
            val success = setInput(context.fixtureClass.java, inputColumn, field, fieldResult)
            rowBuilder.withFieldResult(inputColumn, fieldResult.build())
            allInputsSucceeded = allInputsSucceeded && success
        }

        if (allInputsSucceeded) {
            invokeBeforeFirstCheckMethods(context.fixtureClass.java)
            filter(row, checkHeaders).forEach { (checkColumn, field) ->
                val fieldResult = fieldResults[checkColumn] ?: throw IllegalStateException() // This should never happen
                executeCheck(context.fixtureClass.java, checkColumn, field, fieldResult)
                rowBuilder.withFieldResult(checkColumn, fieldResult.build())
            }
        } else {
            filter(row, checkHeaders).forEach { (checkColumn, _) ->
                val fieldResult = fieldResults[checkColumn] ?: throw IllegalStateException() // This should never happen
                fieldResult.withStatus(Status.Skipped)
                rowBuilder.withFieldResult(checkColumn, fieldResult.build())
            }
        }

    }

    private fun setInput(fixture: Any, header: Header, tableField: Field, fieldResult: FieldResult.Builder): Boolean {
        try {
            doSetInput(fixture, header, tableField)
            fieldResult.withStatus(Status.Executed)
            return true
        } catch (e: AssertionError) {
            fieldResult.withStatus(Status.Failed(e))
        } catch (e: Exception) {
            fieldResult.withStatus(Status.Exception(e))
        }
        return false
    }

    private fun executeCheck(fixture: Any, header: Header, tableField: Field, fieldResult: FieldResult.Builder) {
        try {
            val method = fixtureModel.getCheckMethod(header.name)!!
            fieldResult.withCheckMethod(method)
            doExecuteCheck(fixture, method, tableField)
            this.handleSuccessfulExecution(fieldResult, tableField)
        } catch (e: AssertionError) {
            this.handleAssertionError(fieldResult, tableField, e)
        } catch (e: FixtureMethodInvoker.ExpectedException) {
            this.handleExpectedException(fieldResult, tableField, e)
        } catch (e: Exception) {
            fieldResult.withStatus(Status.Exception(e))
        }
    }

    private fun doExecuteCheck(fixture: Any, method: KCallable<*>, tableField: Field) {
        methodInvoker.invoke(method, fixture, arrayOf(tableField.value))
    }

    private fun doSetInput(fixture: Any, header: Header, tableField: Field) {
        val alias = header.name
        when {
            fixtureModel.isFieldInput(alias) -> setFieldInput(fixture, header, tableField)
            fixtureModel.isMethodInput(alias) -> setMethodInput(fixture, header, tableField)
            else -> throw IllegalStateException() // should never happen
        }
    }

    private fun setFieldInput(fixture: Any, header: Header, tableField: Field) {
        val field = fixtureModel.getInputField(header.name)!!
        fieldInjector.inject(field, fixture, tableField.value)
    }

    private fun setMethodInput(fixture: Any, header: Header, tableField: Field) {
        val method = fixtureModel.getInputMethod(header.name)!!
        methodInvoker.invoke(method, fixture, arrayOf(tableField.value))
    }

    private fun invokeBeforeFirstCheckMethods(fixture: Any) {
        fixtureModel.beforeFirstCheckMethods.forEach { methodInvoker.invoke(it, fixture) }
    }

    private fun invokeAfterRowMethods(fixture: Any) {
        fixtureModel.afterRowMethods.forEach { methodInvoker.invoke(it, fixture) }
    }
    private fun filter(row: Row, headers: Set<Header>): Map<Header, Field> {
        return row.headerToField
            .filterKeys { headers.contains(it) }
    }

    private fun filterHeaders(decisionTable: DecisionTable, predicate: (Header) -> Boolean): Set<Header> {
        return decisionTable.headers.filter(predicate).toSet()
    }

    private fun createFixtureInstance(): Any {
        return context.fixtureClass.java.getDeclaredConstructor().newInstance()
    }

    private fun handleSuccessfulExecution(fieldResult: FieldResult.Builder, tableField: Field) {
        if (tableField.value == ExampleSyntax.EXCEPTION) {
            fieldResult.withStatus(Status.Failed(NoExpectedExceptionThrownException()))
            return
        }
        fieldResult.withStatus(Status.Executed)
    }

    private fun handleAssertionError(fieldResult: FieldResult.Builder, tableField: Field, e: AssertionError) {
        if (tableField.value.isEmpty()) {
            fieldResult.withStatus(Status.ReportActualResult(e.localizedMessage))
            return
        }

        if (tableField.value == ExampleSyntax.EXCEPTION) {
            fieldResult.withStatus(Status.Failed(NoExpectedExceptionThrownException()))
            return
        }
        fieldResult.withStatus(Status.Failed(e))
    }

    private fun handleExpectedException(
        fieldResult: FieldResult.Builder,
        tableField: Field,
        e: FixtureMethodInvoker.ExpectedException
    ) {
        if (tableField.value == ExampleSyntax.EXCEPTION) {
            fieldResult.withStatus(Status.Executed)
            return
        }
        fieldResult.withStatus(Status.Exception(e))
    }

    internal class MalformedDecisionTableFixtureException(fixtureClass: Class<*>, errors: List<String>) :
        RuntimeException(
            "The fixture class <$fixtureClass> is malformed: \n${errors.joinToString(
                separator = "\n",
                prefix = "  - "
            )}"
        )

    internal class UnmappedHeaderException(fixtureClass: Class<*>, unmappedHeaders: List<String>) : RuntimeException(
        "The fixture class <$fixtureClass> has no methods for the following columns: \n${unmappedHeaders.joinToString(
            separator = "\n",
            prefix = "  - "
        )}"
    )
}