package org.livingdoc.results.examples.decisiontables

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.results.Status

internal class RowResultBuilderTest {

    private val h1 = Header("header1")
    private val h2 = Header("header2")

    val fieldResult1 = FieldResult.Builder()
        .withValue("a")
        .withStatus(Status.Executed)
        .build()
    val fieldResult2 = FieldResult.Builder()
        .withValue("b")
        .withStatus(Status.Executed)
        .build()

    private val row: Row = Row(
        mapOf(h1 to Field("a"), h2 to Field("b"))
    )

    @Test
    fun `test successful row result`() {
        val result = RowResult.Builder()
            .withRow(row)
            .withFieldResult(h1, fieldResult1)
            .withFieldResult(h2, fieldResult2)
            .withStatus(Status.Executed)
            .build()

        assertThat(result.status).isEqualTo(Status.Executed)
        assertThat(result.headerToField[h1]).isEqualTo(fieldResult1)
        assertThat(result.headerToField[h2]).isEqualTo(fieldResult2)
    }

    @Test
    fun `test row result with missing field result`() {
        val builder = RowResult.Builder()
            .withRow(row)
            .withFieldResult(h1, fieldResult1)
            .withStatus(Status.Executed)

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test row result with too many field results`() {
        val h3 = Header("header3")
        val fieldResult3 = FieldResult.Builder()
            .withValue("c")
            .withStatus(Status.Executed)
            .build()

        val builder = RowResult.Builder()
            .withRow(row)
            .withFieldResult(h1, fieldResult1)
            .withFieldResult(h2, fieldResult2)
            .withFieldResult(h3, fieldResult3)
            .withStatus(Status.Executed)

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test row result with wrong header`() {
        val h3 = Header("wrong header3")

        val builder = RowResult.Builder()
            .withRow(row)
            .withFieldResult(h1, fieldResult1)
            .withFieldResult(h3, fieldResult2)
            .withStatus(Status.Executed)

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test row result with missing status`() {
        val builder = RowResult.Builder()
            .withRow(row)
            .withFieldResult(h1, fieldResult1)
            .withFieldResult(h2, fieldResult2)

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test finalized builder`() {
        val builder = RowResult.Builder()
            .withRow(row)
            .withFieldResult(h1, fieldResult1)
            .withFieldResult(h2, fieldResult2)
            .withStatus(Status.Executed)

        builder.build()

        assertThrows<IllegalStateException> {
            builder.withStatus(Status.Manual)
        }

        assertThrows<IllegalStateException> {
            builder.withRow(row)
        }

        assertThrows<IllegalStateException> {
            builder.withFieldResult(h1, fieldResult1)
        }
    }

    @Test
    fun `test with unassigned skipped`() {
        val result = RowResult.Builder()
            .withRow(row)
            .withFieldResult(h1, fieldResult1)
            .withUnassignedFieldsSkipped()
            .withStatus(Status.Executed)
            .build()

        assertThat(result.headerToField[h1]).isEqualTo(fieldResult1)
        assertThat(result.headerToField[h2]?.status).isEqualTo(Status.Skipped)
    }

    @Test
    fun `test auto generate step results for manual test`() {
        val result = RowResult.Builder()
            .withRow(row)
            .withStatus(Status.Manual)
            .build()

        assertThat(result.status).isEqualTo(Status.Manual)
        assertThat(result.headerToField[h1]?.status).isEqualTo(Status.Manual)
        assertThat(result.headerToField[h2]?.status).isEqualTo(Status.Manual)
    }
}
