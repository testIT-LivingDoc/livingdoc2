package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input
import org.livingdoc.api.tagging.Tag

/**
 * This [ExecutableDocument] tests interpreting a checkbox from a confluence page as boolean.
 *
 * @see DecisionTableFixture
 * @see ExecutableDocument
 */

@Tag("confluence")
@ExecutableDocument("local://ConfluenceCheckbox.html")
class ConfluenceCheckboxHtml {

    /**
     * This [DecisionTableFixture] will be used to execute the first decision table in the document
     *
     * @see DecisionTableFixture
     */
    @DecisionTableFixture()
    class CalculatorDecisionTableFixture {
        private lateinit var sut: Calculator

        /**
         * This variable will be injected with the value from column "a" in the decision table.
         *
         * @see Input
         */
        @Input("a")
        private var valueA: Boolean = true
        private var valueB: Boolean = true

        /**
         * This method will be used to inject our second variable
         *
         * @param valueB this value be injected from column "b" in the decision table.
         * @see Input
         */
        @Input("b")
        fun setValueB(valueB: Boolean) {
            this.valueB = valueB
        }

        @Check("a and b = ?")
        fun checkSum(expectedValue: Boolean) {
            assertThat(valueA && valueB).isEqualTo(expectedValue)
        }
    }
}
