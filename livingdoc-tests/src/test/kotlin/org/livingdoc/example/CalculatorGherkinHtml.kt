package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.api.tagging.Tag

@Tag("gherkin")
@ExecutableDocument("local://CalculatorGherkin.html")
class CalculatorGherkinHtml {
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

        @Step("result is less than {result}")
        fun `check last result less than`(@Binding("result") result: Float) {
            assertThat(lastResult).isLessThan(result)
        }

        @Step("result is greater than {result}")
        fun `check last result greater than`(@Binding("result") result: Float) {
            assertThat(lastResult).isGreaterThan(result)
        }
    }
    @ScenarioFixture
    class CoffeMachineScenario {
        private lateinit var cm: CoffeeMachine

        @Step("there are {n} coffees left in the machine")
        fun `left coffees`(@Binding("n") n: Int) {
            cm = CoffeeMachine()
            cm.setLeftCoffees(n)
        }

        @Step("I have deposited {n} dollar")
        fun `deposited money`(@Binding("n") n: Float) {
            cm.depositMoney(n)
        }

        @Step("I press the coffee button")
        fun `press button`() {
            cm.triggered = true
        }

        @Step("I should {not} be served a coffee")
        fun `expected output`(
            @Binding("not") not: String?
        ) {
            if (not.isNullOrBlank()) {
                assertThat(cm.getCoffee()).isTrue()
            } else {
                assertThat(cm.getCoffee()).isFalse()
            }
        }
    }
}
