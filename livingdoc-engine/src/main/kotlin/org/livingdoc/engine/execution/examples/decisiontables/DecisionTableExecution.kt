package org.livingdoc.engine.execution.examples.decisiontables

import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.exception.ExampleSyntax
import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.NoExpectedExceptionThrownException
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.execution.examples.decisiontables.model.FieldResult
import org.livingdoc.engine.execution.examples.decisiontables.model.RowResult
import org.livingdoc.engine.execution.examples.executeWithBeforeAndAfter
import org.livingdoc.engine.fixtures.FixtureFieldInjector
import org.livingdoc.engine.fixtures.FixtureMethodInvoker
import org.livingdoc.engine.fixtures.FixtureMethodInvoker.ExpectedException
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row

internal class DecisionTableExecution(
    private val fixtureClass: Class<*>,
    private val decisionTable: DecisionTable,
    document: Any?
) {

    private val fixtureModel = DecisionTableFixtureModel(fixtureClass)
    private val decisionTableResult = DecisionTableResult.Builder().withDecisionTable(decisionTable)

    private val fieldInjector = FixtureFieldInjector(document)
    private val methodInvoker = FixtureMethodInvoker(document)

    /**
     * Executes the configured [DecisionTableResult].
     *
     * Does not throw any kind of exception.
     * Exceptional state of the execution is packages inside the [DecisionTableResult] in
     * the form of different status objects.
     */
    fun execute(): DecisionTableResult {
        if (fixtureClass.isAnnotationPresent(Disabled::class.java)) {
            return decisionTableResult.withStatus(
                Status.Disabled(
                    fixtureClass.getAnnotation(Disabled::class.java).value
                )
            ).build()
        }

        try {
            assertFixtureIsDefinedCorrectly()
            executeTableWithBeforeAndAfter()
            decisionTableResult.withStatus(Status.Executed)
        } catch (e: Exception) {
            decisionTableResult.withStatus(Status.Exception(e))
        } catch (e: AssertionError) {
            decisionTableResult.withStatus(Status.Exception(e))
        }

        decisionTableResult.withUnassignedRowsSkipped()
        return decisionTableResult.build()
    }

    private fun assertFixtureIsDefinedCorrectly() {
        val errors = DecisionTableFixtureChecker.check(fixtureModel)
        if (errors.isNotEmpty()) {
            throw MalformedDecisionTableFixtureException(fixtureClass, errors)
        }

        val unmappedHeaders = findUnmappedHeaders()
        if (unmappedHeaders.isNotEmpty()) {
            throw UnmappedHeaderException(fixtureClass, unmappedHeaders)
        }
    }

    private fun findUnmappedHeaders(): List<String> {
        return decisionTable.headers
            .filter { (name) -> !fixtureModel.isInputAlias(name) && !fixtureModel.isCheckAlias(name) }
            .map { it.name }
    }

    private fun executeTableWithBeforeAndAfter() {
        executeWithBeforeAndAfter(
            before = { invokeBeforeTableMethods() },
            body = { executeTable() },
            after = { invokeAfterTableMethods() }
        )
    }

    private fun executeTable() {
        val inputHeaders = filterHeaders { (name) -> fixtureModel.isInputAlias(name) }
        val checkHeaders = filterHeaders { (name) -> fixtureModel.isCheckAlias(name) }

        decisionTable.rows.forEach { row ->
            val rowResult = RowResult.Builder()
                .withRow(row)
            try {
                executeRowWithBeforeAndAfter(row, rowResult, inputHeaders, checkHeaders)
                rowResult.withStatus(Status.Executed)
            } catch (e: Exception) {
                rowResult.withStatus(Status.Exception(e))
            } catch (e: AssertionError) {
                rowResult.withStatus(Status.Exception(e))
            }

            rowResult.withUnassignedFieldsSkipped()
            decisionTableResult.withRow(rowResult.build())
        }
    }

    private fun executeRowWithBeforeAndAfter(
        row: Row,
        rowResult: RowResult.Builder,
        inputHeaders: Set<Header>,
        checkHeaders: Set<Header>
    ) {
        val fixture = createFixtureInstance()
        executeWithBeforeAndAfter(
            before = { invokeBeforeRowMethods(fixture) },
            body = { executeRow(fixture, row, rowResult, inputHeaders, checkHeaders) },
            after = { invokeAfterRowMethods(fixture) }
        )
    }

    private fun executeRow(
        fixture: Any,
        row: Row,
        rowResult: RowResult.Builder,
        inputHeaders: Set<Header>,
        checkHeaders: Set<Header>
    ) {
        var allInputsSucceeded = true
        val fieldResults = row.headerToField.mapValues {
            FieldResult.Builder().withValue(it.value.value)
        }

        filter(row, inputHeaders).forEach { (inputColumn, field) ->
            val fieldResult = fieldResults[inputColumn] ?: throw IllegalStateException() // This should never happen
            val success = setInput(fixture, inputColumn, field, fieldResult)
            rowResult.withFieldResult(inputColumn, fieldResult.build())
            allInputsSucceeded = allInputsSucceeded && success
        }

        if (allInputsSucceeded) {
            invokeBeforeFirstCheckMethods(fixture)
            filter(row, checkHeaders).forEach { (checkColumn, field) ->
                val fieldResult = fieldResults[checkColumn] ?: throw IllegalStateException() // This should never happen
                executeCheck(fixture, checkColumn, field, fieldResult)
                rowResult.withFieldResult(checkColumn, fieldResult.build())
            }
        } else {
            filter(row, checkHeaders).forEach { (checkColumn, _) ->
                val fieldResult = fieldResults[checkColumn] ?: throw IllegalStateException() // This should never happen
                fieldResult.withStatus(Status.Skipped)
                rowResult.withFieldResult(checkColumn, fieldResult.build())
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
            doExecuteCheck(fixture, header, tableField)
            this.handleSuccessfulExecution(fieldResult, tableField)
        } catch (e: AssertionError) {
            this.handleAssertionError(fieldResult, tableField, e)
        } catch (e: ExpectedException) {
            this.handleExpectedException(fieldResult, tableField, e)
        } catch (e: Exception) {
            fieldResult.withStatus(Status.Exception(e))
        }
    }

    private fun doExecuteCheck(fixture: Any, header: Header, tableField: Field) {
        val method = fixtureModel.getCheckMethod(header.name)!!
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

    private fun invokeBeforeTableMethods() {
        fixtureModel.beforeMethods.forEach { method -> methodInvoker.invokeStatic(method) }
    }

    private fun invokeBeforeRowMethods(fixture: Any) {
        fixtureModel.beforeRowMethods.forEach { methodInvoker.invoke(it, fixture) }
    }

    private fun invokeBeforeFirstCheckMethods(fixture: Any) {
        fixtureModel.beforeFirstCheckMethods.forEach { methodInvoker.invoke(it, fixture) }
    }

    private fun invokeAfterRowMethods(fixture: Any) {
        fixtureModel.afterRowMethods.forEach { methodInvoker.invoke(it, fixture) }
    }

    private fun invokeAfterTableMethods() {
        fixtureModel.afterMethods.forEach { method -> methodInvoker.invokeStatic(method) }
    }

    private fun filter(row: Row, headers: Set<Header>): Map<Header, Field> {
        return row.headerToField
            .filterKeys { headers.contains(it) }
    }

    private fun filterHeaders(predicate: (Header) -> Boolean): Set<Header> {
        return decisionTable.headers.filter(predicate).toSet()
    }

    private fun createFixtureInstance(): Any {
        return fixtureClass.getDeclaredConstructor().newInstance()
    }

    private fun handleSuccessfulExecution(fieldResult: FieldResult.Builder, tableField: Field) {
        if (tableField.value == ExampleSyntax.EXCEPTION) {
            fieldResult.withStatus(Status.Failed(NoExpectedExceptionThrownException()))
            return
        }
        fieldResult.withStatus(Status.Executed)
    }

    private fun handleAssertionError(fieldResult: FieldResult.Builder, tableField: Field, e: AssertionError) {
        if (tableField.value == ExampleSyntax.EXCEPTION) {
            fieldResult.withStatus(Status.Failed(NoExpectedExceptionThrownException()))
            return
        }
        fieldResult.withStatus(Status.Failed(e))
    }

    private fun handleExpectedException(fieldResult: FieldResult.Builder, tableField: Field, e: ExpectedException) {
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
