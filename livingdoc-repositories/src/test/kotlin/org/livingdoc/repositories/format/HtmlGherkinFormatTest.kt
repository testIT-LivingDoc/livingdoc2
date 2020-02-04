package org.livingdoc.repositories.format

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.repositories.format.HtmlGherkinFormatTestData.emptyHtml
import org.livingdoc.repositories.format.HtmlGherkinFormatTestData.getHtmlGherkin2
import org.livingdoc.repositories.format.HtmlGherkinFormatTestData.getHtmlGherkinTableWithOnlyOneRow
import org.livingdoc.repositories.format.HtmlGherkinFormatTestData.htmlGherkinGiven
import org.livingdoc.repositories.format.HtmlGherkinFormatTestData.htmlGherkinThen
import org.livingdoc.repositories.model.scenario.Scenario

class HtmlGherkinFormatTest {

    private val cut = HtmlFormat()

    @Test
    fun `empty html files are ignored`() {
        val result = cut.parse(emptyHtml())
        assertThat(result.elements).hasSize(0)
    }

    @Test
    fun `when is detected`() {
        val result = cut.parse(getHtmlGherkinTableWithOnlyOneRow())
        assertThat(result.elements).hasSize(1)
    }

    @Test
    fun `but is detected`() {
        val result = cut.parse(getHtmlGherkinTableWithOnlyOneRow())
        assertThat(result.elements).hasSize(1)
    }

    @Test
    fun `Given is detected`() {
        val result = cut.parse(htmlGherkinGiven())
        assertThat(result.elements).hasSize(1)
    }

    @Test
    fun `Then is detected`() {
        val result = cut.parse(htmlGherkinThen())
        assertThat(result.elements).hasSize(1)
    }

    @Test
    fun `multiple features are detected`() {
        val result = cut.parse(getHtmlGherkin2())
        assertThat(result.elements).hasSize(2)
    }

    @Test fun `manual test headline is parsed before gherkin`() {
        val htmlDocument = cut.parse(HtmlGherkinFormatTestData.getHtmlGherkinManualList())

        assertThat(htmlDocument.elements[0].description.name).isEqualTo("MANUAL Test1")
    }

    @Test
    fun `descriptive text is parsed before gherkin`() {
        val htmlDocument = cut.parse(HtmlGherkinFormatTestData.getHtmlGherkinDescriptionText())

        assertThat(htmlDocument.elements[0].description.descriptiveText).isEqualTo("This is a descriptive text.\nThis is another descriptive text.")
    }

    @Test
    fun `can parse simple scenario from html`() {
        val htmlDocument = cut.parse(HtmlGherkinFormatTestData.getHtmlGherkinSimple())

        assertThat(htmlDocument.elements).hasOnlyOneElementSatisfying { element ->
            assertThat(element.description).satisfies { description ->
                assertThat(description.name).isEqualTo("Test Scenario")
                assertThat(description.isManual).isFalse()
            }

            assertThat(element).isInstanceOfSatisfying(Scenario::class.java) { scenario ->
                assertThat(scenario.steps).hasOnlyOneElementSatisfying { step ->
                    assertThat(step.value).isEqualTo("I test the Gherkin parser")
                }
            }
        }
    }
}
