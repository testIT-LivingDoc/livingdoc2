package org.livingdoc.engine.execution.examples.decisiontables

import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.livingdoc.engine.execution.Result.*
import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableExecution.*
import org.livingdoc.engine.execution.examples.decisiontables.model.*
import org.mockito.ArgumentMatchers.anyString


internal class DecisionTableExecutorTest {

    val cut = DecisionTableExecutor()

    @BeforeEach fun reset() {
        LifeCycleFixture.reset()
        ExtendedLifeCycleFixture.reset()
    }

    @Test fun `life cycle of simple fixture`() {
        val input = Header("input")
        val check = Header("check")
        val headers = arrayListOf(input, check)

        val row1 = Row(mapOf(input to Field("r1i"), check to Field("r1c")))
        val row2 = Row(mapOf(input to Field("r2i"), check to Field("r2c")))
        val rows = arrayListOf(row1, row2)

        val resultTable = cut.execute(DecisionTable(headers, rows), LifeCycleFixture::class.java, null)
        assertThat(resultTable.result).isInstanceOf(Executed::class.java)

        val fixture = LifeCycleFixture.callback
        with(inOrder(fixture)) {
            verify(fixture).beforeTable()
            verify(fixture).beforeRow()
            verify(fixture).input("r1i")
            verify(fixture).beforeFirstCheck()
            verify(fixture).check("r1c")
            verify(fixture).afterRow()
            verify(fixture).beforeRow()
            verify(fixture).input("r2i")
            verify(fixture).beforeFirstCheck()
            verify(fixture).check("r2c")
            verify(fixture).afterRow()
            verify(fixture).afterTable()
            verifyNoMoreInteractions()
        }
    }

    @Test fun `malformed fixtures throw a special exception class`() {
        val headers = emptyList<Header>()
        val rows = arrayListOf(Row(emptyMap()))
        val decisionTable = DecisionTable(headers, rows)
        val fixtureClass = MalformedFixtures.NoDefaultConstructor::class.java

        val tableResult = cut.execute(decisionTable, fixtureClass, null).result
        assertThat(tableResult).isInstanceOf(Exception::class.java)

        val exception = (tableResult as Exception).exception
        assertThat(exception).isInstanceOf(MalformedDecisionTableFixtureException::class.java)
    }

    @Test fun `unmapped headers throw special exception class`() {
        val input = Header("input")
        val check = Header("check")
        val unknown = Header("unknown")

        val headers = listOf(input, check, unknown)
        val rows = emptyList<Row>()
        val decisionTable = DecisionTable(headers, rows)
        val fixtureClass = LifeCycleFixture::class.java

        val tableResult = cut.execute(decisionTable, fixtureClass, null).result
        assertThat(tableResult).isInstanceOf(Exception::class.java)

        val exception = (tableResult as Exception).exception
        assertThat(exception).isInstanceOf(UnmappedHeaderException::class.java)
        assertThat(exception).hasMessageContaining("- unknown")

        val fixture = LifeCycleFixture.callback
        verify(fixture, never()).beforeTable()
    }

    @Nested inner class `life cycle methods can exist multiple times` {

        val callback = ExtendedLifeCycleFixture.callback!!

        @Test fun `before table`() {
            execute()
            verify(callback).beforeTable1()
            verify(callback).beforeTable2()
        }

        @Test fun `before row`() {
            execute()
            verify(callback).beforeRow1()
            verify(callback).beforeRow2()
        }

        @Test fun `before first check`() {
            execute()
            verify(callback).beforeFirstCheck1()
            verify(callback).beforeFirstCheck2()
        }

        @Test fun `after row`() {
            execute()
            verify(callback).afterRow1()
            verify(callback).afterRow2()
        }

        @Test fun `after table`() {
            execute()
            verify(callback).afterTable1()
            verify(callback).afterTable2()
        }

        private fun execute(): DecisionTableResult {
            val input = Header("input")
            val check = Header("check")
            val headers = arrayListOf(input, check)
            val row = Row(mapOf(input to Field("r1i"), check to Field("r1c")))
            val decisionTable = DecisionTable(headers, listOf(row))
            return cut.execute(decisionTable, ExtendedLifeCycleFixture::class.java, null)
        }

    }

    @Nested inner class `exceptional behaviour` {

        val input = Header("input")
        val check = Header("check")
        val headers = arrayListOf(input, check)

        val row = Row(mapOf(input to Field("r1i"), check to Field("r1c")))
        val anotherRow = Row(mapOf(input to Field("r2i"), check to Field("r2c")))

        val callback = LifeCycleFixture.callback!!

        @Test fun `exception in one row does not effect execution of another`() {
            val exception = IllegalStateException()
            given { callback.input("r1i") } willThrow { exception }

            execute(row, anotherRow)

            with(inOrder(callback)) {
                verify(callback).beforeTable()
                verify(callback).beforeRow()
                verify(callback).input("r1i")
                verify(callback).afterRow()
                verify(callback).beforeRow()
                verify(callback).input("r2i")
                verify(callback).beforeFirstCheck()
                verify(callback).check("r2c")
                verify(callback).afterRow()
                verify(callback).afterTable()
                verifyNoMoreInteractions()
            }
        }

        @Test fun `assertion error in one row does not effect execution of another`() {
            val exception = AssertionError()
            given { callback.input("r1i") } willThrow { exception }

            execute(row, anotherRow)

            with(inOrder(callback)) {
                verify(callback).beforeTable()
                verify(callback).beforeRow()
                verify(callback).input("r1i")
                verify(callback).afterRow()
                verify(callback).beforeRow()
                verify(callback).input("r2i")
                verify(callback).beforeFirstCheck()
                verify(callback).check("r2c")
                verify(callback).afterRow()
                verify(callback).afterTable()
                verifyNoMoreInteractions()
            }
        }

        @Nested inner class `input methods` {

            @Test fun `exception will set exception result for input`() {
                val exception = IllegalStateException()
                given { callback.input(anyString()) } willThrow { exception }

                val inputField = execute(row).rows[0].headerToField[input]!!

                assertThat(inputField.result).isInstanceOf(Exception::class.java)
            }

            @Test fun `exception will prevent checks from being executed`() {
                val exception = IllegalStateException()
                given { callback.input(anyString()) } willThrow { exception }

                execute(row)

                verify(callback, never()).beforeFirstCheck()
                verify(callback, never()).check(anyString())
                verify(callback).afterRow()
                verify(callback).afterTable()
            }

            @Test fun `assertion error will set failed result for input`() {
                val exception = AssertionError()
                given { callback.input(anyString()) } willThrow { exception }

                val inputField = execute(row).rows[0].headerToField[input]!!

                assertThat(inputField.result).isInstanceOf(Failed::class.java)
            }

            @Test fun `assertion error will prevent checks from being executed`() {
                val exception = AssertionError()
                given { callback.input(anyString()) } willThrow { exception }

                execute(row)

                verify(callback, never()).beforeFirstCheck()
                verify(callback, never()).check(anyString())
                verify(callback).afterRow()
                verify(callback).afterTable()
            }

        }

        @Nested inner class `check methods` {

            @Test fun `exception will set exception result for check`() {
                val exception = IllegalStateException()
                given { callback.check(anyString()) } willThrow { exception }

                val checkField = execute(row).rows[0].headerToField[check]!!

                assertThat(checkField.result).isInstanceOf(Exception::class.java)
            }

            @Test fun `exception will still execute after row methods`() {
                val exception = IllegalStateException()
                given { callback.check(anyString()) } willThrow { exception }

                execute(row)

                verify(callback).afterRow()
                verify(callback).afterTable()
            }

            @Test fun `assertion error will set failed result for check`() {
                val exception = AssertionError()
                given { callback.check(anyString()) } willThrow { exception }

                val checkField = execute(row).rows[0].headerToField[check]!!

                assertThat(checkField.result).isInstanceOf(Failed::class.java)
            }

            @Test fun `assertion error will still execute after row methods`() {
                val exception = AssertionError()
                given { callback.check(anyString()) } willThrow { exception }

                execute(row)

                verify(callback).afterRow()
                verify(callback).afterTable()
            }

        }

        @Nested inner class `before row methods` {

            @Test fun `exception will set exception result on row`() {
                val exception = IllegalStateException()
                given { callback.beforeRow() } willThrow { exception }

                val row = execute(row).rows[0]

                assertThat(row.result).isInstanceOf(Exception::class.java)
            }

            @Test fun `exception will set skipped result for remaining fields of row`() {
                val exception = IllegalStateException()
                given { callback.beforeRow() } willThrow { exception }

                val rowResult = execute(row).rows[0]

                assertThat(rowResult.headerToField[input]!!.result).isInstanceOf(Skipped::class.java)
                assertThat(rowResult.headerToField[check]!!.result).isInstanceOf(Skipped::class.java)
            }

            @Test fun `exception will prevent any further row actions from being executed`() {
                val exception = IllegalStateException()
                given { callback.beforeRow() } willThrow { exception }

                execute(row)

                verify(callback, never()).input(anyString())
                verify(callback, never()).beforeFirstCheck()
                verify(callback, never()).check(anyString())
                verify(callback).afterRow()
                verify(callback).afterTable()
            }

            @Test fun `assertion error will set exception result on row`() {
                val exception = AssertionError()
                given { callback.beforeRow() } willThrow { exception }

                val row = execute(row).rows[0]

                assertThat(row.result).isInstanceOf(Exception::class.java)
            }

            @Test fun `assertion error will set skipped result for remaining fields of row`() {
                val exception = AssertionError()
                given { callback.beforeRow() } willThrow { exception }

                val rowResult = execute(row).rows[0]

                assertThat(rowResult.headerToField[input]!!.result).isInstanceOf(Skipped::class.java)
                assertThat(rowResult.headerToField[check]!!.result).isInstanceOf(Skipped::class.java)
            }

            @Test fun `assertion error will prevent any further row actions from being executed`() {
                val exception = AssertionError()
                given { callback.beforeRow() } willThrow { exception }

                execute(row)

                verify(callback, never()).input(anyString())
                verify(callback, never()).beforeFirstCheck()
                verify(callback, never()).check(anyString())
                verify(callback).afterRow()
                verify(callback).afterTable()
            }

        }

        @Nested inner class `before first check methods` {

            @Test fun `exception will set exception result on row`() {
                val exception = IllegalStateException()
                given { callback.beforeFirstCheck() } willThrow { exception }

                val row = execute(row).rows[0]

                assertThat(row.result).isInstanceOf(Exception::class.java)
            }

            @Test fun `exception will set skipped result for remaining fields of row`() {
                val exception = IllegalStateException()
                given { callback.beforeFirstCheck() } willThrow { exception }

                val rowResult = execute(row).rows[0]

                assertThat(rowResult.headerToField[check]!!.result).isInstanceOf(Skipped::class.java)
            }

            @Test fun `exception will prevent any further row actions from being executed`() {
                val exception = IllegalStateException()
                given { callback.beforeFirstCheck() } willThrow { exception }

                execute(row)

                verify(callback, never()).check(anyString())
                verify(callback).afterRow()
                verify(callback).afterTable()
            }

            @Test fun `assertion error will set exception result on row`() {
                val exception = AssertionError()
                given { callback.beforeFirstCheck() } willThrow { exception }

                val row = execute(row).rows[0]

                assertThat(row.result).isInstanceOf(Exception::class.java)
            }

            @Test fun `assertion error will set skipped result for remaining fields of row`() {
                val exception = AssertionError()
                given { callback.beforeFirstCheck() } willThrow { exception }

                val rowResult = execute(row).rows[0]

                assertThat(rowResult.headerToField[check]!!.result).isInstanceOf(Skipped::class.java)
            }

            @Test fun `assertion error will prevent any further row actions from being executed`() {
                val exception = AssertionError()
                given { callback.beforeFirstCheck() } willThrow { exception }

                execute(row)

                verify(callback, never()).check(anyString())
                verify(callback).afterRow()
                verify(callback).afterTable()
            }

        }

        @Nested inner class `after row methods` {

            @Test fun `exception will set exception result on row`() {
                val exception = IllegalStateException()
                given { callback.afterRow() } willThrow { exception }

                val row = execute(row).rows[0]

                assertThat(row.result).isInstanceOf(Exception::class.java)
            }

            @Test fun `exception will prevent any further row actions from being executed`() {
                val exception = IllegalStateException()
                given { callback.afterRow() } willThrow { exception }

                execute(row)

                verify(callback).afterTable()
            }

            @Test fun `assertion error will set exception result on row`() {
                val exception = AssertionError()
                given { callback.afterRow() } willThrow { exception }

                val row = execute(row).rows[0]

                assertThat(row.result).isInstanceOf(Exception::class.java)
            }

            @Test fun `assertion error will prevent any further row actions from being executed`() {
                val exception = AssertionError()
                given { callback.afterRow() } willThrow { exception }

                execute(row)

                verify(callback).afterTable()
            }

        }

        @Nested inner class `before table methods` {

            @Test fun `exception will set exception result on table`() {
                val exception = IllegalStateException()
                given { callback.beforeTable() } willThrow { exception }

                val table = execute(row)

                assertThat(table.result).isInstanceOf(Exception::class.java)
            }

            @Test fun `exception will set skipped result for all rows`() {
                val exception = IllegalStateException()
                given { callback.beforeTable() } willThrow { exception }

                val table = execute(row, anotherRow)

                val rows = table.rows
                assertThat(rows[0].result).isInstanceOf(Skipped::class.java)
                assertThat(rows[1].result).isInstanceOf(Skipped::class.java)
            }

            @Test fun `exception will prevent any row being executed`() {
                val exception = IllegalStateException()
                given { callback.beforeTable() } willThrow { exception }

                execute(row, anotherRow)

                verify(callback, never()).beforeRow()
            }

            @Test fun `assertion error will set exception result on row`() {
                val exception = AssertionError()
                given { callback.beforeTable() } willThrow { exception }

                val table = execute(row)

                assertThat(table.result).isInstanceOf(Exception::class.java)
            }

            @Test fun `assertion error will set skipped result for all rows`() {
                val exception = AssertionError()
                given { callback.beforeTable() } willThrow { exception }

                val table = execute(row, anotherRow)

                val rows = table.rows
                assertThat(rows[0].result).isInstanceOf(Skipped::class.java)
                assertThat(rows[1].result).isInstanceOf(Skipped::class.java)
            }

            @Test fun `assertion error will prevent any row being executed`() {
                val exception = AssertionError()
                given { callback.beforeTable() } willThrow { exception }

                execute(row, anotherRow)

                verify(callback, never()).beforeRow()
            }

        }

        @Nested inner class `after table methods` {

            @Test fun `exception will set exception result on table`() {
                val exception = IllegalStateException()
                given { callback.afterTable() } willThrow { exception }

                val table = execute(row)

                assertThat(table.result).isInstanceOf(Exception::class.java)
            }

            @Test fun `assertion error will set exception result on row`() {
                val exception = AssertionError()
                given { callback.afterTable() } willThrow { exception }

                val table = execute(row)

                assertThat(table.result).isInstanceOf(Exception::class.java)
            }

        }

        private fun execute(vararg rows: Row): DecisionTableResult {
            val decisionTable = DecisionTable(headers, rows.asList())
            return cut.execute(decisionTable, LifeCycleFixture::class.java, null)
        }

    }

    @Test fun `smoke test of good case fixture`() {

        val valueAColumn = Header("a")
        val valueBColumn = Header("b")
        val sumColumn = Header("a + b = ?")
        val diffColumn = Header("a - b = ?")
        val multiplyColumn = Header("a * b = ?")
        val divideColumn = Header("a / b = ?")
        val headers = arrayListOf(valueAColumn, valueBColumn, sumColumn, diffColumn, multiplyColumn, divideColumn)

        val rows = arrayListOf(
                Row(mapOf(
                        valueAColumn to Field("0"),
                        valueBColumn to Field("0"),
                        sumColumn to Field("0"),
                        diffColumn to Field("0"),
                        multiplyColumn to Field("0"),
                        divideColumn to Field("NaN")
                )),
                Row(mapOf(
                        valueAColumn to Field("1"),
                        valueBColumn to Field("0"),
                        sumColumn to Field("1"),
                        diffColumn to Field("1"),
                        multiplyColumn to Field("0"),
                        divideColumn to Field("Infinity")
                )),
                Row(mapOf(
                        valueAColumn to Field("0"),
                        valueBColumn to Field("1"),
                        sumColumn to Field("1"),
                        diffColumn to Field("-1"),
                        multiplyColumn to Field("0"),
                        divideColumn to Field("0")
                )),
                Row(mapOf(
                        valueAColumn to Field("1"),
                        valueBColumn to Field("1"),
                        sumColumn to Field("2"),
                        diffColumn to Field("0"),
                        multiplyColumn to Field("1"),
                        divideColumn to Field("1")
                )))

        val resultTable = cut.execute(DecisionTable(headers, rows), CalculatorDecisionTableFixture::class.java, null)

        assertThat(resultTable.result).isEqualTo(Executed)
        assertThat(resultTable.rows).hasSize(4)
        resultTable.rows.forEach { row ->
            assertThat(row.result).isEqualTo(Executed)
            row.headerToField.values.forEach { field ->
                assertThat(field.result).isEqualTo(Executed)
            }
        }

    }

}