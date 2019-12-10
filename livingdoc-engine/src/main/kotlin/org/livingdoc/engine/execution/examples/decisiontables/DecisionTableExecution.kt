package org.livingdoc.engine.execution.examples.decisiontables

import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.exception.ExampleSyntax
import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.execution.examples.decisiontables.model.FieldResult
import org.livingdoc.engine.execution.examples.decisiontables.model.RowResult
import org.livingdoc.engine.execution.examples.executeWithBeforeAndAfter
import org.livingdoc.engine.fixtures.FixtureFieldInjector
import org.livingdoc.engine.fixtures.FixtureMethodInvoker
import org.livingdoc.engine.fixtures.FixtureMethodInvoker.ExpectedException
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Header

internal class DecisionTableExecution(
    private val fixtureClass: Class<*>,
    decisionTable: DecisionTable,
    document: Any?
) {

    private val fixtureModel = DecisionTableFixtureModel(fixtureClass)
    private val decisionTableResult = DecisionTableResult.from(decisionTable)

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
            markTableAsDisabled(fixtureClass.getAnnotation(Disabled::class.java).value)
            return decisionTableResult
        }

        try {
            assertFixtureIsDefinedCorrectly()
            executeTableWithBeforeAndAfter()
            markTableAsSuccessfullyExecuted()
        } catch (e: Exception) {
            markTableAsExecutedWithException(e)
        } catch (e: AssertionError) {
            markTableAsExecutedWithException(e)
        }
        setSkippedStatusForAllUnknownResults()
        return decisionTableResult
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
        return decisionTableResult.headers
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
        val inputHeaders = filterHeaders({ (name) -> fixtureModel.isInputAlias(name) })
        val checkHeaders = filterHeaders({ (name) -> fixtureModel.isCheckAlias(name) })
        decisionTableResult.rows.forEach { row ->
            try {
                executeRowWithBeforeAndAfter(row, inputHeaders, checkHeaders)
                markRowAsSuccessfullyExecuted(row)
            } catch (e: Exception) {
                markRowAsExecutedWithException(row, e)
            } catch (e: AssertionError) {
                markRowAsExecutedWithException(row, e)
            }
        }
    }

    private fun executeRowWithBeforeAndAfter(row: RowResult, inputHeaders: Set<Header>, checkHeaders: Set<Header>) {
        val fixture = createFixtureInstance()
        executeWithBeforeAndAfter(
            before = { invokeBeforeRowMethods(fixture) },
            body = { executeRow(fixture, row, inputHeaders, checkHeaders) },
            after = { invokeAfterRowMethods(fixture) }
        )
    }

    private fun executeRow(fixture: Any, row: RowResult, inputHeaders: Set<Header>, checkHeaders: Set<Header>) {
        var allInputsSucceeded = true
        filter(row, inputHeaders).forEach { inputColumn, tableField ->
            val success = setInput(fixture, inputColumn, tableField)
            allInputsSucceeded = allInputsSucceeded && success
        }

        if (allInputsSucceeded) {
            invokeBeforeFirstCheckMethods(fixture)
            filter(row, checkHeaders).forEach { checkColumn, tableField ->
                executeCheck(fixture, checkColumn, tableField)
            }
        }
    }

    private fun setInput(fixture: Any, header: Header, tableField: FieldResult): Boolean {
        try {
            doSetInput(fixture, header, tableField)
            markFieldAsSuccessfullyExecuted(tableField)
            return true
        } catch (e: AssertionError) {
            markFieldAsExecutedWithFailure(tableField, e)
        } catch (e: Exception) {
            markFieldAsExecutedWithException(tableField, e)
        }
        return false
    }

    private fun executeCheck(fixture: Any, header: Header, tableField: FieldResult) {
        try {
            doExecuteCheck(fixture, header, tableField)
            markFieldAsSuccessfullyExecuted(tableField)
        } catch (e: AssertionError) {
            markFieldAsExecutedWithFailure(tableField, e)
        } catch (e: ExpectedException) {
            if (tableField.value == ExampleSyntax.EXCEPTION) {
                markFieldAsSuccessfullyExecuted(tableField)
                return
            }
            markFieldAsExecutedWithException(tableField, e)
        } catch (e: Exception) {
            markFieldAsExecutedWithException(tableField, e)
        }
    }

    private fun isExpectedException(e: Exception): Boolean {
        return e is ExpectedException
    }

    private fun doSetInput(fixture: Any, header: Header, tableField: FieldResult) {
        val alias = header.name
        when {
            fixtureModel.isFieldInput(alias) -> setFieldInput(fixture, header, tableField)
            fixtureModel.isMethodInput(alias) -> setMethodInput(fixture, header, tableField)
            else -> throw IllegalStateException() // should never happen
        }
    }

    private fun setSkippedStatusForAllUnknownResults() {
        decisionTableResult.rows.forEach { row ->
            row.headerToField.values.forEach { field ->
                if (field.status === Status.Unknown) {
                    field.status = Status.Skipped
                }
            }
            if (row.status === Status.Unknown) {
                row.status = Status.Skipped
            }
        }
    }

    private fun setFieldInput(fixture: Any, header: Header, tableField: FieldResult) {
        val field = fixtureModel.getInputField(header.name)!!
        fieldInjector.inject(field, fixture, tableField.value)
    }

    private fun setMethodInput(fixture: Any, header: Header, tableField: FieldResult) {
        val method = fixtureModel.getInputMethod(header.name)!!
        methodInvoker.invoke(method, fixture, arrayOf(tableField.value))
    }

    private fun invokeBeforeTableMethods() {
        fixtureModel.beforeTableMethods.forEach { method -> methodInvoker.invokeStatic(method) }
    }

    private fun createFixtureInstance(): Any {
        return fixtureClass.getDeclaredConstructor().newInstance()
    }

    private fun invokeBeforeRowMethods(fixture: Any) {
        fixtureModel.beforeRowMethods.forEach { methodInvoker.invoke(it, fixture) }
    }

    private fun filter(row: RowResult, headers: Set<Header>): Map<Header, FieldResult> {
        return row.headerToField.filterKeys { headers.contains(it) }
    }

    private fun invokeBeforeFirstCheckMethods(fixture: Any) {
        fixtureModel.beforeFirstCheckMethods.forEach { methodInvoker.invoke(it, fixture) }
    }

    private fun doExecuteCheck(fixture: Any, header: Header, tableField: FieldResult) {
        val method = fixtureModel.getCheckMethod(header.name)!!
        methodInvoker.invoke(method, fixture, arrayOf(tableField.value))
    }

    private fun invokeAfterRowMethods(fixture: Any) {
        fixtureModel.afterRowMethods.forEach { methodInvoker.invoke(it, fixture) }
    }

    private fun invokeAfterTableMethods() {
        fixtureModel.afterTableMethods.forEach { method -> methodInvoker.invokeStatic(method) }
    }

    private fun filterHeaders(predicate: (Header) -> Boolean): Set<Header> {
        return decisionTableResult.headers.filter(predicate).toSet()
    }

    private fun markFieldAsExecutedWithFailure(tableField: FieldResult, e: AssertionError) {
        tableField.status = Status.Failed(e)
    }

    private fun markFieldAsExecutedWithException(tableField: FieldResult, e: Exception) {
        tableField.status = Status.Exception(e)
    }

    private fun markFieldAsSuccessfullyExecuted(tableField: FieldResult) {
        tableField.status = Status.Executed
    }

    private fun markRowAsSuccessfullyExecuted(row: RowResult) {
        row.status = Status.Executed
    }

    private fun markRowAsExecutedWithException(row: RowResult, e: Throwable) {
        row.status = Status.Exception(e)
    }

    private fun markTableAsDisabled(reason: String) {
        decisionTableResult.status = Status.Disabled(reason)
    }

    private fun markTableAsSuccessfullyExecuted() {
        decisionTableResult.status = Status.Executed
    }

    private fun markTableAsExecutedWithException(e: Throwable) {
        decisionTableResult.status = Status.Exception(e)
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
