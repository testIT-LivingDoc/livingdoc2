package org.livingdoc.results.examples.decisiontables

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.livingdoc.results.Status
import java.lang.reflect.Method

internal class FieldResultBuilderTest {

    private val checkMethod: Method = (FixtureClass::class.java).methods[0]

    @Test
    fun `test successful field result`() {
        val result = FieldResult.Builder()
            .withValue("a")
            .withCheckMethod(this.checkMethod)
            .withStatus(Status.Executed)
            .build()

        assertThat(result.value).isEqualTo("a")
        assertThat(result.method).isEqualTo(this.checkMethod)
        assertThat(result.status).isEqualTo(Status.Executed)
    }

    @Test
    fun `test field result with missing value`() {
        val builder = FieldResult.Builder()
            .withCheckMethod(this.checkMethod)
            .withStatus(Status.Executed)

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test field result with missing status`() {
        val builder = FieldResult.Builder()
            .withValue("a")
            .withCheckMethod(this.checkMethod)

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test field result with missing method`() {
        val result = FieldResult.Builder()
            .withValue("a")
            .withStatus(Status.Executed)
            .build()

        assertThat(result.method).isEqualTo(null)
    }

    @Test
    fun `test finalized builder`() {
        val builder = FieldResult.Builder()
            .withValue("a")
            .withCheckMethod(this.checkMethod)
            .withStatus(Status.Executed)

        builder.build()

        assertThrows<IllegalStateException> {
            builder.withStatus(Status.Manual)
        }
    }

    class FixtureClass {
        fun fixtureMethod() {}
    }
}
