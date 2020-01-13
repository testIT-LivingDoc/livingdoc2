package org.livingdoc.results.examples.scenarios

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.livingdoc.results.Status
import java.lang.reflect.Method

internal class StepResultBuilderTest {

    private val fixtureMethod: Method = (FixtureClass::class.java).methods[0]

    @Test
    fun `test successful step result`() {
        val result = StepResult.Builder()
            .withValue("a")
            .withFixtureMethod(this.fixtureMethod)
            .withStatus(Status.Executed)
            .build()

        assertThat(result.value).isEqualTo("a")
        assertThat(result.fixtureMethod).isEqualTo(this.fixtureMethod)
        assertThat(result.status).isEqualTo(Status.Executed)
    }

    @Test
    fun `test step result with missing value`() {
        val builder = StepResult.Builder()
            .withFixtureMethod(this.fixtureMethod)
            .withStatus(Status.Executed)

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test step result with missing status`() {
        val builder = StepResult.Builder()
            .withValue("a")
            .withFixtureMethod(this.fixtureMethod)

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test step result with missing method`() {
        val result = StepResult.Builder()
            .withValue("a")
            .withStatus(Status.Executed)
            .build()

        assertThat(result.fixtureMethod).isEqualTo(null)
    }

    @Test
    fun `test finalized builder`() {
        val builder = StepResult.Builder()
            .withValue("a")
            .withFixtureMethod(this.fixtureMethod)
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
