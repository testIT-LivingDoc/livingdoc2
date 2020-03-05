package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.BeforeRow
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.api.tagging.Tag

/**
 * This [ExecutableDocument] tests the [Calculator] SUT using [DecisionTableFixtures][DecisionTableFixture].
 *
 * @see DecisionTableFixture
 * @see ExecutableDocument
 */
@Tag("html")
@ExecutableDocument("local://Calculator.html")
class CalculatorDocumentHtml {

    @ScenarioFixture
    class CalculatorScenario {
        private var lastResult: Float? = null
        private lateinit var cut: Calculator

        @Step("a calculator")
        fun `initialize calculator`() {
            cut = Calculator()
        }

        @Step("I add {lhs} and {rhs}")
        fun `add two numbers`(@Binding("lhs") lhs: Float, @Binding("rhs") rhs: Float) {
            lastResult = cut.sum(lhs, rhs)
        }

        @Step("I get {result}")
        fun `check last result`(@Binding("result") result: Float) {
            assertThat(lastResult).isEqualTo(result)
        }
    }

    /**
     * This [DecisionTableFixture] will be used to execute the first decision table in the document
     *
     * @see DecisionTableFixture
     */
    @DecisionTableFixture(parallel = true)
    class CalculatorDecisionTableFixture {
        private lateinit var sut: Calculator

        /**
         * This variable will be injected with the value from column "a" in the decision table.
         *
         * @see Input
         */
        @Input("a")
        private var valueA: Float = 0f
        private var valueB: Float = 0f

        /**
         * This function will be executed before each row. It initializes our SUT.
         *
         * @see BeforeRow
         */
        @BeforeRow
        fun beforeRow() {
            sut = Calculator()
        }

        /**
         * This method will be used to inject our second variable
         *
         * @param valueB this value be injected from column "b" in the decision table.
         * @see Input
         */
        @Input("b")
        fun setValueB(valueB: Float) {
            this.valueB = valueB
        }

        /**
         * This check will be executed for the column "a + b = ?" in the decision table.
         *
         * @param expectedValue this value will be injected from column "a + b = ?" in the decision table
         * @see Check
         */
        @Check("a + b = ?")
        fun checkSum(expectedValue: Float) {
            val result = sut.sum(valueA, valueB)
            assertThat(result).isEqualTo(expectedValue)
        }

        /**
         * This check will be executed for the column "a - b = ?" in the decision table.
         *
         * @param expectedValue this value will be injected from column "a - b = ?" in the decision table
         * @see Check
         */
        @Check("a - b = ?")
        fun checkDiff(expectedValue: Float) {
            val result = sut.diff(valueA, valueB)
            assertThat(result).isEqualTo(expectedValue)
        }

        /**
         * This check will be executed for the column "a * b = ?" in the decision table.
         *
         * @param expectedValue this value will be injected from column "a * b = ?" in the decision table
         * @see Check
         */
        @Check("a * b = ?")
        fun checkMultiply(expectedValue: Float) {
            val result = sut.multiply(valueA, valueB)
            assertThat(result).isEqualTo(expectedValue)
        }

        /**
         * This check will be executed for the column "a / b = ?" in the decision table.
         *
         * @param expectedValue this value will be injected from column "a / b = ?" in the decision table
         * @see Check
         */
        @Check("a / b = ?")
        fun checkDivide(expectedValue: Float) {
            val result = sut.divide(valueA, valueB)
            assertThat(result).isEqualTo(expectedValue)
        }
    }

    @DecisionTableFixture(parallel = true)
    class CalculatorDecisionTableFixture2 {

        private lateinit var sut: Calculator

        @Input("x")
        private var valueA: Float = 0f
        private var valueB: Float = 0f

        @BeforeRow
        fun beforeRow() {
            sut = Calculator()
        }

        @Input("y")
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
}
