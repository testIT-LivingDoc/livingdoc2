package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.scenarios.Before
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step

@ExecutableDocument("local://Calculator.md")
class CalculatorDocumentDisabledScenarioFixtures {

    // Should be executed
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

        @Step("subtraction {b} from {a} equals {c}")
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

        // Should be executed
        @ScenarioFixture
        class CalculatorExtendedScenarioFixture {
            private lateinit var sut: Calculator

            @Step.Steps(Step("add {a} to itself and you get {b}"), Step("divide it by {c} and you get {d}"))
            fun extended(
                @Binding("a") a: Float,
                @Binding("b") b: Float,
                @Binding("c") c: Float,
                @Binding("d") d: Float
            ) {
                val step1 = sut.sum(a, a)
                assertThat(step1).isEqualTo(b)

                val step2 = sut.divide(b, c)
                assertThat(step2).isEqualTo(d)
            }
        }
    }

    // Should be executed
    @ScenarioFixture
    class CalculatorScenarioFixture2 {

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

        @Step("subtraction {b} from {a} equals {c}")
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

        // Should not be executed
        @Disabled("Extended Disabled Scenario Fixture")
        @ScenarioFixture
        class DisabledCalculatorExtendedScenarioFixture {
            private lateinit var sut: Calculator

            @Step.Steps(Step("add {a} to itself and you get {b}"), Step("divide it by {c} and you get {d}"))
            fun extended(
                @Binding("a") a: Float,
                @Binding("b") b: Float,
                @Binding("c") c: Float,
                @Binding("d") d: Float
            ) {
                val step1 = sut.sum(a, a)
                assertThat(step1).isEqualTo(b)

                val step2 = sut.divide(b, c)
                assertThat(step2).isEqualTo(d)
            }
        }
    }

    // Should not be executed
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

        // Should not be executed
        @ScenarioFixture
        class DisabledCalculatorExtendedScenarioFixture {
            private lateinit var sut: Calculator

            @Step.Steps(Step("add {a} to itself and you get {b}"), Step("divide it by {c} and you get {d}"))
            fun extended(
                @Binding("a") a: Float,
                @Binding("b") b: Float,
                @Binding("c") c: Float,
                @Binding("d") d: Float
            ) {
                val step1 = sut.sum(a, a)
                assertThat(step1).isEqualTo(b)

                val step2 = sut.divide(b, c)
                assertThat(step2).isEqualTo(d)
            }
        }
    }

}
