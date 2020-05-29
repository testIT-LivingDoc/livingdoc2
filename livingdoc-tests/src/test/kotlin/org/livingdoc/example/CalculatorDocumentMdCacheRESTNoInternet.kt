package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.BeforeRow
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input
import org.livingdoc.api.tagging.Tag

/**
 * Can be used to test if caching works for the rest api without internet.
 * Disabled because it fails if there is no cached file.
 */
@Tag("markdown")
@ExecutableDocument("rest://TestingCache.html")
@Disabled
class CalculatorDocumentMdRest {

    @DecisionTableFixture()
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
}
