package org.livingdoc.engine.execution.examples.decisiontables

import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.exception.ExampleSyntax
import org.livingdoc.engine.LivingDoc
import org.livingdoc.engine.execution.examples.NoExpectedExceptionThrownException
import org.livingdoc.engine.execution.examples.executeWithBeforeAndAfter
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.engine.fixtures.FixtureFieldInjector
import org.livingdoc.engine.fixtures.FixtureMethodInvoker
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.results.Status
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import org.livingdoc.results.examples.decisiontables.FieldResult
import org.livingdoc.results.examples.decisiontables.RowResult
import java.lang.reflect.Method
import org.livingdoc.engine.LivingDoc.Companion.executor

/**
 * Obviously wraps a decision table fixture
 */
class DecisionTableFixtureWrapper(
    val fixtureClass: Class<*>
) : Fixture<DecisionTable> {

    private val fixtureModel = DecisionTableFixtureModel(fixtureClass)

    private val fieldInjector = FixtureFieldInjector(null)
    private val methodInvoker = FixtureMethodInvoker(null)

    /**
     * Executes the wrapped fixture class with the give decision table. Does not throw any kind of exception.
     * Exceptional state of the execution is packages inside the [DecisionTableResult] in the form of different status
     * objects.
     *
     * @param testData A decision table instance that can be mapped to the wrapped fixture
     * @return A DecisionTableResult for the execution
     */
    override fun execute(testData: DecisionTable): DecisionTableResult {
        val decisionTableResult =
            DecisionTableResult.Builder().withDecisionTable(testData)
                .withFixtureSource(fixtureClass)

        when {
            LivingDoc.failFastActivated -> {
                decisionTableResult.withUnassignedRowsSkipped()
            }
            fixtureClass.isAnnotationPresent(Disabled::class.java) -> {
                decisionTableResult.withStatus(Status.Disabled(fixtureClass.getAnnotation(Disabled::class.java).value))
            }
            else -> {
                try {
                    assertFixtureIsDefinedCorrectly(testData)
                    executeTableWithBeforeAndAfter(testData).forEach {
                        decisionTableResult.withRow(it)
                    }
                    decisionTableResult.withStatus(Status.Executed)
                } catch (e: Exception) {
                    decisionTableResult.withStatus(Status.Exception(e))
                } catch (e: AssertionError) {
                    decisionTableResult.withStatus(Status.Exception(e))
                }

                decisionTableResult.withUnassignedRowsSkipped()
            }
        }
        return decisionTableResult.build()
    }

    private fun assertFixtureIsDefinedCorrectly(decisionTable: DecisionTable) {
        val errors = DecisionTableFixtureChecker.check(fixtureModel)
        if (errors.isNotEmpty()) {
            throw MalformedDecisionTableFixtureException(fixtureClass, errors)
        }

        val unmappedHeaders = findUnmappedHeaders(decisionTable)
        if (unmappedHeaders.isNotEmpty()) {
            throw UnmappedHeaderException(fixtureClass, unmappedHeaders)
        }
    }

    private fun findUnmappedHeaders(decisionTable: DecisionTable): List<String> {
        return decisionTable.headers
            .filter { (name) -> !fixtureModel.isInputAlias(name) && !fixtureModel.isCheckAlias(name) }
            .map { it.name }
    }

    private fun executeTableWithBeforeAndAfter(decisionTable: DecisionTable): List<RowResult> {
        return executeWithBeforeAndAfter(
            before = { invokeBeforeTableMethods() },
            body = { executeTable(decisionTable) },
            after = { invokeAfterTableMethods() }
        )
    }

    private fun executeTable(decisionTable: DecisionTable): List<RowResult> {
        val inputHeaders = filterHeaders(decisionTable) { (name) -> fixtureModel.isInputAlias(name) }
        val checkHeaders = filterHeaders(decisionTable) { (name) -> fixtureModel.isCheckAlias(name) }

        val executeRow = { row: Row ->
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
            rowResult.build()
        }

        return if (fixtureModel.parallelExecution) {
            decisionTable.rows.map { { executeRow(it) } }.map { executor.submit(it) }.map { it.get() }
        } else {
            decisionTable.rows.map(executeRow)
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

    private fun doExecuteCheck(fixture: Any, method: Method, tableField: Field) {
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

    private fun filterHeaders(decisionTable: DecisionTable, predicate: (Header) -> Boolean): Set<Header> {
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
