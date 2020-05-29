package org.livingdoc.example

import org.assertj.core.api.Assertions
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.api.tagging.Tag

@Tag("markdown")
@ExecutableDocument("local://TestTexts.md", group = GroupedDocuments::class)
class ExternalGroupedDocument {
    @ScenarioFixture
    class ScenarioTests {
        @Step("concatenate {a} and {b} will result in {c}")
        fun concString(
            @Binding("a") a: String,
            @Binding("b") b: String,
            @Binding("c") c: String
        ) {
            val result = GroupedDocuments.sut.concStrings(a, b)
            Assertions.assertThat(result).isEqualTo(c)
        }

        @Step("nullifying {a} and {b} will give us {c} as output")
        fun nullStringing(
            @Binding("a") a: String,
            @Binding("b") b: String,
            @Binding("c") c: String
        ) {
            val result = GroupedDocuments.sut.nullifyString()
            val res2 = GroupedDocuments.sut.concStrings(a, b)
            Assertions.assertThat(res2).isNotEqualTo(c)
            Assertions.assertThat(result).isEqualTo(c)
        }
    }
}
