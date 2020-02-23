package org.livingdoc.example

import org.assertj.core.api.Assertions
import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.documents.Group
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.api.tagging.Tag
import org.livingdoc.example.GroupedDocuments.Companion.sut

@Group
class GroupedDocuments {

    @Tag("markdown")
    @ExecutableDocument("local://TestTexts.md")
    class GroupedDocument1 {
        @ScenarioFixture
        class ScenarioTests {
            @Step("concatenate {a} and {b} will result in {c}")
            fun concString(
                @Binding("a") a: String,
                @Binding("b") b: String,
                @Binding("c") c: String
            ) {
                val result = sut.concStrings(a, b)
                Assertions.assertThat(result).isEqualTo(c)
            }

            @Step("nullifying {a} and {b} will give us {c} as output")
            fun nullStringing(
                @Binding("a") a: String,
                @Binding("b") b: String,
                @Binding("c") c: String
            ) {
                val result = sut.nullifyString()
                val res2 = sut.concStrings(a, b)
                Assertions.assertThat(res2).isNotEqualTo(c)
                Assertions.assertThat(result).isEqualTo(c)
            }
        }
    }

    @ExecutableDocument("local://TestTexts.md")
    class GroupedDocument2 {
        @ScenarioFixture
        class ScenarioTests {
            @Step("concatenate {a} and {b} will result in {c}")
            fun concString(
                @Binding("a") a: String,
                @Binding("b") b: String,
                @Binding("c") c: String
            ) {
                val result = sut.concStrings(a, b)
                Assertions.assertThat(result).isEqualTo(c)
            }

            @Step("nullifying {a} and {b} will give us {c} as output")
            fun nullStringing(
                @Binding("a") a: String,
                @Binding("b") b: String,
                @Binding("c") c: String
            ) {
                val result = sut.nullifyString()
                val res2 = sut.concStrings(a, b)
                Assertions.assertThat(res2).isNotEqualTo(c)
                Assertions.assertThat(result).isEqualTo(c)
            }
        }
    }

    companion object {
        lateinit var sut: TextFunctions

        @Before
        fun setUp() {
            sut = TextFunctions()
            println("Before group of documents")
        }

        @After
        fun cleanUp() {
            println("After group of documents")
        }
    }
}

@ExecutableDocument("local://TestTexts.md", group = GroupedDocuments::class)
class GroupedDocument3 {
    @ScenarioFixture
    class ScenarioTests {
        @Step("concatenate {a} and {b} will result in {c}")
        fun concString(
            @Binding("a") a: String,
            @Binding("b") b: String,
            @Binding("c") c: String
        ) {
            val result = sut.concStrings(a, b)
            Assertions.assertThat(result).isEqualTo(c)
        }

        @Step("nullifying {a} and {b} will give us {c} as output")
        fun nullStringing(
            @Binding("a") a: String,
            @Binding("b") b: String,
            @Binding("c") c: String
        ) {
            val result = sut.nullifyString()
            val res2 = sut.concStrings(a, b)
            Assertions.assertThat(res2).isNotEqualTo(c)
            Assertions.assertThat(result).isEqualTo(c)
        }
    }
}
