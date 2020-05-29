package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.livingdoc.api.Before
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.BeforeRow
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.api.tagging.Tag

@Tag("markdown")
@ExecutableDocument("local://CalculatorInt.md")
class CalculatorIntDocumentMdException {

    @DecisionTableFixture
    class CalculatorDecisionTableFixture {

        private lateinit var sut: CalculatorInt

        @Input("a")
        private var valueA = 0
        private var valueB = 0

        @BeforeRow
        fun beforeRow() {
            sut = CalculatorInt()
        }

        @Input("b")
        fun setValueB(valueB: Int) {
            this.valueB = valueB
        }

        @Check("a * b = ?")
        fun checkMultiply(expectedValue: Int) {
            val result = sut.multiply(valueA, valueB)
            assertThat(result).isEqualTo(expectedValue)
        }

        @Check("a / b = ?")
        fun checkDivide(expectedValue: Int?) {
            val result = sut.divide(valueA, valueB)
            assertThat(result).isEqualTo(expectedValue)
        }
    }

    @ScenarioFixture
    class CalculatorScenarioFixture {

        private lateinit var sut: CalculatorInt

        @Before
        fun before() {
            sut = CalculatorInt()
        }

        @Step("adding {a} and {b} equals {c}")
        fun add(
            @Binding("a") a: Int,
            @Binding("b") b: Int,
            @Binding("c") c: Int
        ) {
            val result = sut.sum(a, b)
            assertThat(result).isEqualTo(c)
        }

        @Step("subtraction {b} form {a} equals {c}")
        fun subtract(
            @Binding("a") a: Int,
            @Binding("b") b: Int,
            @Binding("c") c: Int
        ) {
            val result = sut.diff(a, b)
            assertThat(result).isEqualTo(c)
        }

        @Step("multiplying {a} and {b} equals {c}")
        fun multiply(
            @Binding("a") a: Int,
            @Binding("b") b: Int,
            @Binding("c") c: Int
        ) {
            val result = sut.multiply(a, b)
            assertThat(result).isEqualTo(c)
        }

        @Step("dividing {a} by {b} equals {c}")
        fun divide(
            @Binding("a") a: Int,
            @Binding("b") b: Int,
            @Binding("c") c: Int?
        ) {
            val result = sut.divide(a, b)
            assertThat(result).isEqualTo(c)
        }

        @Step("add {a} to itself and you get {b}")
        fun selfadd(
            @Binding("a") a: Int,
            @Binding("b") b: Int
        ) {
            val result = sut.sum(a, a)
            assertThat(result).isEqualTo(b)
        }
    }
}
