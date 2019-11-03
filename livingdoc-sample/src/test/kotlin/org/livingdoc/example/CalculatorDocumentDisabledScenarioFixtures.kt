package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.scenarios.Before
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step

@ExecutableDocument("local://Calculator.md")
class CalculatorDocumentDisabledScenarioFixtures {

    @Disabled("Disabled ScenarioFixture")
    @ScenarioFixture
    class DisabledCalculatorScenarioFixture {

        private lateinit var sut: Calculator

        @Before
        fun before() {
            sut = Calculator()
        }

        @Step("adding {a} and {b} equals {c}")
        fun add(
            @Binding("a") a: Float,
            @Binding("b") b: Float,
            @Binding("c") c: Float
        ) {
            val result = sut.sum(a, b)
            assertThat(result).isEqualTo(c)
        }

        @Step("subtraction {b} form {a} equals {c}")
        fun subtract(
            @Binding("a") a: Float,
            @Binding("b") b: Float,
            @Binding("c") c: Float
        ) {
            val result = sut.diff(a, b)
            assertThat(result).isEqualTo(c)
        }

        @Step("multiplying {a} and {b} equals {c}")
        fun multiply(
            @Binding("a") a: Float,
            @Binding("b") b: Float,
            @Binding("c") c: Float
        ) {
            val result = sut.multiply(a, b)
            assertThat(result).isEqualTo(c)
        }

        @Step("dividing {a} by {b} equals {c}")
        fun divide(
            @Binding("a") a: Float,
            @Binding("b") b: Float,
            @Binding("c") c: Float
        ) {
            val result = sut.divide(a, b)
            assertThat(result).isEqualTo(c)
        }

        @Step("add {a} to itself and you get {b}")
        fun selfadd(
            @Binding("a") a: Float,
            @Binding("b") b: Float
        ) {
            val result = sut.sum(a, a)
            assertThat(result).isEqualTo(b)
        }
    }
}
