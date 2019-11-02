package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.BeforeRow
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input

@ExecutableDocument("local://Calculator.md")
class CalculatorDocumentDisabledDecisionTableFixtures {

    @Disabled("Disabled DecisionTableFixture")
    @DecisionTableFixture
    class DisabledCalculatorDecisionTableFixture {

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
    }
}
