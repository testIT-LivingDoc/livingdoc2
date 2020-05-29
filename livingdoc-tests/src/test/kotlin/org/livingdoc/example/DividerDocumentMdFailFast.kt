package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.livingdoc.api.Before
import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.documents.FailFast
import org.livingdoc.api.fixtures.decisiontables.BeforeRow
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.api.tagging.Tag

/**
 * [ExecutableDocuments][ExecutableDocument] can also be specified in Markdown
 *
 * @see ExecutableDocument
 */
@Tag("markdown")
@Disabled
@FailFast(onExceptionTypes = [IllegalArgumentException::class])
@ExecutableDocument("local://DividerFailFast.md")
class DividerDocumentMdFailFast {

    @DecisionTableFixture
    class DividerDecisionTableFixture {

        private lateinit var sut: Divider

        @Input("a")
        private var valueA: Float = 0f
        private var valueB: Float = 0f

        @BeforeRow
        fun beforeRow() {
            sut = Divider()
        }

        @Input("b")
        fun setValueB(valueB: Float) {
            this.valueB = valueB
        }

        @Check("a / b = ?")
        fun checkDivide(expectedValue: Float) {
                val result = sut.divide(valueA, valueB)
                assertThat(result).isEqualTo(expectedValue)
        }
    }

    @ScenarioFixture
    class DividerScenarioFixture {

        private lateinit var sut: Divider

        @Before
        fun before() {
            sut = Divider()
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
