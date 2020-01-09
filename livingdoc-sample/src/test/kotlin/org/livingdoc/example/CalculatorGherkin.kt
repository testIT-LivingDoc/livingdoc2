package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step

@ExecutableDocument("local://Calculator.feature")
class CalculatorGherkin {
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
}
