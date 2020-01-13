package org.livingdoc.results.examples.decisiontables

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.results.Status
import org.livingdoc.results.examples.scenarios.ScenarioResultBuilderTest

internal class DecisionTableResultBuilderTest {
    private val fixtureClass: Class<*> = ScenarioResultBuilderTest.FixtureClass::class.java

    private val h1 = Header("header1")
    private val h2 = Header("header2")

    private val row1: Row = Row(mapOf(h1 to Field("a"), h2 to Field("b")))
    private val row2: Row = Row(mapOf(h1 to Field("c"), h2 to Field("d")))

    private val emptyDecisionTable = DecisionTable(emptyList(), emptyList())
    private val decisionTable = DecisionTable(listOf(h1, h2), listOf(row1, row2))

    private val rowResult1 = RowResult.Builder()
        .withRow(row1)
        .withFieldResult(
            h1, FieldResult.Builder()
                .withValue("a")
                .withStatus(Status.Executed)
                .build()
        )
        .withFieldResult(
            h2, FieldResult.Builder()
                .withValue("b")
                .withStatus(Status.Executed)
                .build()
        )
        .withStatus(Status.Executed)
        .build()

    private val rowResult2 = RowResult.Builder()
        .withRow(row2)
        .withFieldResult(
            h1, FieldResult.Builder()
                .withValue("c")
                .withStatus(Status.Executed)
                .build()
        )
        .withFieldResult(
            h2, FieldResult.Builder()
                .withValue("d")
                .withStatus(Status.Executed)
                .build()
        )
        .withStatus(Status.Executed)
        .build()

    @Test
    fun `test decision table with no rows`() {
        val result = DecisionTableResult.Builder()
            .withDecisionTable(emptyDecisionTable)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)
            .build()

        assertThat(result.decisionTable).isEqualTo(emptyDecisionTable)
        assertThat(result.fixtureSource).isEqualTo(fixtureClass)
        assertThat(result.status).isEqualTo(Status.Executed)
        assertThat(result.headers).hasSize(0)
        assertThat(result.rows).hasSize(0)
    }

    @Test
    fun `test decision table with rows`() {
        val result = DecisionTableResult.Builder()
            .withDecisionTable(decisionTable)
            .withRow(rowResult1)
            .withRow(rowResult2)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)
            .build()

        assertThat(result.decisionTable).isEqualTo(decisionTable)
        assertThat(result.headers).hasSize(2)
        assertThat(result.rows).hasSize(2)
        assertThat(result.rows[0]).isEqualTo(rowResult1)
        assertThat(result.rows[1]).isEqualTo(rowResult2)
    }

    @Test
    fun `test decision table result with missing row result`() {
        val builder = DecisionTableResult.Builder()
            .withDecisionTable(decisionTable)
            .withRow(rowResult1)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test decision table result with too many row results`() {
        val row3 = Row(mapOf(h1 to Field("e"), h2 to Field("f")))
        val rowResult3 = RowResult.Builder()
            .withRow(row3)
            .withFieldResult(
                h1, FieldResult.Builder()
                    .withValue("e")
                    .withStatus(Status.Executed)
                    .build()
            )
            .withFieldResult(
                h2, FieldResult.Builder()
                    .withValue("f")
                    .withStatus(Status.Executed)
                    .build()
            )
            .withStatus(Status.Executed)
            .build()

        val builder = DecisionTableResult.Builder()
            .withDecisionTable(decisionTable)
            .withRow(rowResult1)
            .withRow(rowResult2)
            .withRow(rowResult3)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test decision table result with wrong row result`() {
        val row3 = Row(mapOf(h1 to Field("e"), h2 to Field("f")))
        val rowResult3 = RowResult.Builder()
            .withRow(row3)
            .withFieldResult(
                h1, FieldResult.Builder()
                    .withValue("e")
                    .withStatus(Status.Executed)
                    .build()
            )
            .withFieldResult(
                h2, FieldResult.Builder()
                    .withValue("f")
                    .withStatus(Status.Executed)
                    .build()
            )
            .withStatus(Status.Executed)
            .build()

        val builder = DecisionTableResult.Builder()
            .withDecisionTable(decisionTable)
            .withRow(rowResult1)
            .withRow(rowResult3)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test decision table result with missing status`() {
        val builder = DecisionTableResult.Builder()
            .withDecisionTable(emptyDecisionTable)
            .withFixtureSource(fixtureClass)

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test decision table result with missing decision table`() {
        val builder = DecisionTableResult.Builder()
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test decision table result with missing fixture`() {
        val result = DecisionTableResult.Builder()
            .withDecisionTable(emptyDecisionTable)
            .withStatus(Status.Executed)
            .build()

        assertThat(result.fixtureSource).isNull()
    }

    @Test
    fun `test finalized builder`() {
        val builder = DecisionTableResult.Builder()
            .withDecisionTable(decisionTable)
            .withRow(rowResult1)
            .withRow(rowResult2)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)

        builder.build()

        assertThrows<IllegalStateException> {
            builder.withStatus(Status.Manual)
        }

        assertThrows<IllegalStateException> {
            builder.withRow(rowResult1)
        }

        assertThrows<IllegalStateException> {
            builder.withDecisionTable(decisionTable)
        }

        assertThrows<IllegalStateException> {
            builder.withFixtureSource(fixtureClass)
        }
    }

    @Test
    fun `test with unassigned skipped`() {
        val result = DecisionTableResult.Builder()
            .withDecisionTable(decisionTable)
            .withRow(rowResult1)
            .withUnassignedRowsSkipped()
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)
            .build()

        assertThat(result.rows).hasSize(2)
        assertThat(result.rows[0]).isEqualTo(rowResult1)
        assertThat(result.rows[1].status).isEqualTo(Status.Skipped)
    }

    @Test
    fun `test auto generate row results for manual test`() {
        val result = DecisionTableResult.Builder()
            .withDecisionTable(decisionTable)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Manual)
            .build()

        assertThat(result.status).isEqualTo(Status.Manual)
        assertThat(result.rows).hasSize(2)
        assertThat(result.rows[0].status).isEqualTo(Status.Manual)
        assertThat(result.rows[1].status).isEqualTo(Status.Manual)
    }

    class FixtureClass
}
