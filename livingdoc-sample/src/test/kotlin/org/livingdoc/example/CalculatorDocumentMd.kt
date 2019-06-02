package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.BeforeRow
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input
import org.livingdoc.api.fixtures.scenarios.Before
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step

@ExecutableDocument("local://Calculator.md")
class CalculatorDocumentMd {

    @DecisionTableFixture
    class CalculatorDecisionTableFixture {

        private lateinit var sut: Calculator

        @Input("a")
        private var valueA: Float = 0f
        private var valueB: Float = 0f

        @BeforeRow
        fun beforeRow() {
            sut = Calculator()
        }

        @Input("b")
        fun setValueB(valueB: Float) {
            this.valueB = valueB
        }

        @Check("a + b = ?")
        fun checkSum(expectedValue: Float) {
            val result = sut.sum(valueA, valueB)
            assertThat(result).isEqualTo(expectedValue)
        }

        @Check("a - b = ?")
        fun checkDiff(expectedValue: Float) {
            val result = sut.diff(valueA, valueB)
            assertThat(result).isEqualTo(expectedValue)
        }

        @Check("a * b = ?")
        fun checkMultiply(expectedValue: Float) {
            val result = sut.multiply(valueA, valueB)
            assertThat(result).isEqualTo(expectedValue)
        }

        @Check("a / b = ?")
        fun checkDivide(expectedValue: Float) {
            val result = sut.divide(valueA, valueB)
            assertThat(result).isEqualTo(expectedValue)
        }
    }

    @ScenarioFixture
    class CalculatorScenarioFixture {

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
    }
}
