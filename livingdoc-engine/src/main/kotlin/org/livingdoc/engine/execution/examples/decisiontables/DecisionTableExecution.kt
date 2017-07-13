package org.livingdoc.engine.execution.examples.decisiontables

import org.livingdoc.engine.execution.Result
import org.livingdoc.engine.execution.examples.decisiontables.model.*
import org.livingdoc.engine.execution.examples.executeWithBeforeAndAfter
import org.livingdoc.engine.fixtures.FixtureFieldInjector
import org.livingdoc.engine.fixtures.FixtureMethodInvoker

internal class DecisionTableExecution(
        private val fixtureClass: Class<*>,
        decisionTable: DecisionTable,
        document: Any?
) {

    private val fixtureModel = DecisionTableFixtureModel(fixtureClass)
    private val decisionTable = DecisionTableResult.from(decisionTable)

    private val fieldInjector = FixtureFieldInjector(document)
    private val methodInvoker = FixtureMethodInvoker(document)

    /**
     * Executes the configured [DecisionTableResult].
     *
     * Does not throw any kind of exception. Exceptional state of the execution is packages inside the [DecisionTableResult] in
     * the form of different result objects.
     */
    fun execute(): DecisionTableResult {
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
        return decisionTable
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
        val inputHeaders = filterHeaders({ (name) -> fixtureModel.isInputAlias(name) })
        val checkHeaders = filterHeaders({ (name) -> fixtureModel.isCheckAlias(name) })
        decisionTable.rows.forEach { row ->
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
        } catch (e: Exception) {
            markFieldAsExecutedWithException(tableField, e)
        }
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
        decisionTable.rows.forEach { row ->
            row.headerToField.values.forEach { field ->
                if (field.result is Result.Unknown) {
                    field.result = Result.Skipped
                }
            }
            if (row.result is Result.Unknown) {
                row.result = Result.Skipped
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
        return fixtureClass.newInstance()
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
        return decisionTable.headers.filter(predicate).toSet()
    }

    private fun markFieldAsExecutedWithFailure(tableField: FieldResult, e: AssertionError) {
        tableField.result = Result.Failed(e)
    }

    private fun markFieldAsExecutedWithException(tableField: FieldResult, e: Exception) {
        tableField.result = Result.Exception(e)
    }

    private fun markFieldAsSuccessfullyExecuted(tableField: FieldResult) {
        tableField.result = Result.Executed
    }

    private fun markRowAsSuccessfullyExecuted(row: RowResult) {
        row.result = Result.Executed
    }

    private fun markRowAsExecutedWithException(row: RowResult, e: Throwable) {
        row.result = Result.Exception(e)
    }

    private fun markTableAsSuccessfullyExecuted() {
        decisionTable.result = Result.Executed
    }

    private fun markTableAsExecutedWithException(e: Throwable) {
        decisionTable.result = Result.Exception(e)
    }

    internal class MalformedDecisionTableFixtureException(fixtureClass: Class<*>, errors: List<String>)
        : RuntimeException("The fixture class <$fixtureClass> is malformed: \n${errors.joinToString(separator = "\n", prefix = "  - ")}")

    internal class UnmappedHeaderException(fixtureClass: Class<*>, unmappedHeaders: List<String>)
        : RuntimeException("The fixture class <$fixtureClass> has no methods for the following columns: \n${unmappedHeaders.joinToString(separator = "\n", prefix = "  - ")}")

}