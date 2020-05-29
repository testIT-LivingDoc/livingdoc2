package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.livingdoc.api.Before
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step

/**
 * This [ExecutableDocument] demonstrates the implementation of [ScenarioFixtures][ScenarioFixture].
 *
 * @see ExecutableDocument
 * @see ScenarioFixture
 * @see TextFunctions
 */
@ExecutableDocument("local://TestTexts.md")
class TextFunctionsMD {
    /**
     * This [ScenarioFixture] will be used to run lists in the [ExecutableDocument].
     *
     * @see ExecutableDocument
     * @see ScenarioFixture
     */
    @ScenarioFixture
    class ScenarioTests {
        private lateinit var sut: TextFunctions

        /**
         * This function will be run before any steps of the scenario are executed.
         *
         * @see Before
         */
        @Before
        fun before() {
            sut = TextFunctions()
        }

        /**
         * This function will be called for all steps matching the given step template.
         *
         * @param a will be set to the text matching `{a}` in the step template
         * @param b will be set to the text matching `{b}` in the step template
         * @param c will be set to the text matching `{c}` in the step template
         * @see Binding
         * @see Step
         */
        @Step("concatenate {a} and {b} will result in {c}")
        fun concString(
            @Binding("a") a: String,
            @Binding("b") b: String,
            @Binding("c") c: String
        ) {
            val result = sut.concStrings(a, b)
            assertThat(result).isEqualTo(c)
        }

        /**
         * This function will be called for all steps matching the given step template.
         *
         * @param a will be set to the text matching `{a}` in the step template
         * @param b will be set to the text matching `{b}` in the step template
         * @param c will be set to the text matching `{c}` in the step template
         * @see Binding
         * @see Step
         */
        @Step("nullifying {a} and {b} will give us {c} as output")
        fun nullStringing(
            @Binding("a") a: String,
            @Binding("b") b: String,
            @Binding("c") c: String
        ) {
            val result = sut.nullifyString()
            val res2 = sut.concStrings(a, b)
            assertThat(res2).isNotEqualTo(c)
            assertThat(result).isEqualTo(c)
        }
    }
}
