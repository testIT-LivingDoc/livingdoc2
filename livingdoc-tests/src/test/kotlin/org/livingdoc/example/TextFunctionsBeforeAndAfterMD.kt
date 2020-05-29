package org.livingdoc.example

import org.assertj.core.api.Assertions.assertThat
import org.livingdoc.api.Before
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step

@ExecutableDocument("local://TestTexts.md")
class TextFunctionsBeforeAndAfterMD {
    companion object {
        private lateinit var sut: TextFunctions

        @JvmStatic
        @Before
        fun before() {
            sut = TextFunctions()
        }
    }

    @ScenarioFixture
    class ScenarioTests {

        @Step("concatenate {a} and {b} will result in {c}")
        fun concString(
            @Binding("a") a: String,
            @Binding("b") b: String,
            @Binding("c") c: String
        ) {
            val result = sut.concStrings(a, b)
            assertThat(result).isEqualTo(c)
        }

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
