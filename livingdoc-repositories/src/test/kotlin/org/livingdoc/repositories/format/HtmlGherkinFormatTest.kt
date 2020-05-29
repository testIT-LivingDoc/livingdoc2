package org.livingdoc.repositories.format

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.livingdoc.repositories.format.HtmlGherkinFormatTestData.emptyHtml
import org.livingdoc.repositories.format.HtmlGherkinFormatTestData.getDescriptiveHtml
import org.livingdoc.repositories.format.HtmlGherkinFormatTestData.getHtmlGherkin2
import org.livingdoc.repositories.format.HtmlGherkinFormatTestData.getHtmlGherkinTableWithOnlyOneRow
import org.livingdoc.repositories.format.HtmlGherkinFormatTestData.getPTags
import org.livingdoc.repositories.format.HtmlGherkinFormatTestData.getheaderOutsideOfPre
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
    fun descriptions() {
        val result = cut.parse(getDescriptiveHtml())
        assertThat(result.elements[0].description.descriptiveText).isEqualTo("descriptive text is here.")
    }

    @Test
    fun `multiple features are detected`() {
        val result = cut.parse(getHtmlGherkin2())
        assertThat(result.elements).hasSize(2)
    }

    @Test
    fun `manual test headline is parsed before gherkin`() {
        val htmlDocument = cut.parse(HtmlGherkinFormatTestData.getHtmlGherkinManualList())

        assertThat(htmlDocument.elements[0].description.name).isEqualTo("MANUAL Test1")
    }

    @Test
    fun `descriptive text is parsed before gherkin`() {
        val htmlDocument = cut.parse(HtmlGherkinFormatTestData.getHtmlGherkinDescriptionText())

        assertThat(htmlDocument.elements[0].description.descriptiveText).isEqualTo("This is a descriptive text." +
                "\nThis is another descriptive text.")
    }

    @Test
    fun `exception if headline between gherkin tags`() {
        Assertions.assertThrows(io.cucumber.gherkin.ParserException.CompositeParserException::class.java) {
            cut.parse(HtmlGherkinFormatTestData.headlineBetweenGherkin())
        }
    }

    @Test
    fun `Ignores headline if headline between pre tags`() {
        val htmlDocument = cut.parse(HtmlGherkinFormatTestData.headlineBetweenPre())

        assertThat(htmlDocument.elements[0].description.name).isEqualTo(null + "\n" + "Test Scenario 1")
    }

    @Test
    fun `header outside pre`() {
        val htmldocument = cut.parse(getheaderOutsideOfPre())

        assertThat(htmldocument.elements[0].description.name).isEqualTo("This is a header\n" +
                "Test Scenario 1")
    }

    @Test
    fun `parse descriptive text from html`() {
        val htmldocument = cut.parse(getPTags())

        assertThat(htmldocument.elements[0].description.descriptiveText).isEqualTo("This is a description 1\n" +
                "This is a description 2")
    }

    @Test
    fun `can parse simple scenario from html`() {
        val htmlDocument = cut.parse(HtmlGherkinFormatTestData.getHtmlGherkinSimple())

        assertThat(htmlDocument.elements).hasOnlyOneElementSatisfying { element ->
            assertThat(element.description).satisfies { description ->
                assertThat(description.name).isEqualTo(null + "\n" + "Test Scenario")
                assertThat(description.isManual).isFalse()
            }

            assertThat(element).isInstanceOfSatisfying(Scenario::class.java) { scenario ->
                assertThat(scenario.steps).hasOnlyOneElementSatisfying { step ->
                    assertThat(step.value).isEqualTo("I test the Gherkin parser")
                }
            }
        }
    }

    @Test
    fun `can parse multiple scenarios from html`() {
        val htmlDocument = cut.parse(HtmlGherkinFormatTestData.getHtmlGherkinMultipleScenario())

        assertThat(htmlDocument.elements).hasSize(2)
        assertThat(htmlDocument.elements[0]).satisfies { testData ->
            assertThat(testData.description).satisfies { description ->
                assertThat(description.name).isEqualTo(null + "\n" + "Test Scenario 1")
                assertThat(description.isManual).isFalse()
            }

            assertThat(testData).isInstanceOfSatisfying(Scenario::class.java) { scenario ->
                assertThat(scenario.steps).hasOnlyOneElementSatisfying { step ->
                    assertThat(step.value).isEqualTo("I test the Gherkin parser")
                }
            }
        }
        assertThat(htmlDocument.elements[1]).satisfies { testData ->
            assertThat(testData.description).satisfies { description ->
                assertThat(description.name).isEqualTo(null + "\n" + "Test Scenario 2")
                assertThat(description.isManual).isFalse()
            }

            assertThat(testData).isInstanceOfSatisfying(Scenario::class.java) { scenario ->
                assertThat(scenario.steps).hasOnlyOneElementSatisfying { step ->
                    assertThat(step.value).isEqualTo("I test the Gherkin parser again")
                }
            }
        }
    }

    @Test
    fun `can parse multiple steps in a scenario from html`() {
        val htmlDocument = cut.parse(HtmlGherkinFormatTestData.getHtmlGherkinMultipleSteps())

        assertThat(htmlDocument.elements).hasOnlyOneElementSatisfying { testData ->
            assertThat(testData.description).satisfies { description ->
                assertThat(description.name).isEqualTo(null + "\n" + "Test Scenario 1")
                assertThat(description.isManual).isFalse()
            }

            assertThat(testData).isInstanceOfSatisfying(Scenario::class.java) { scenario ->
                assertThat(scenario.steps).satisfies { steps ->
                    assertThat(steps).hasSize(5)
                    assertThat(steps[0]).satisfies { step ->
                        assertThat(step.value).isEqualTo("a working Gherkin parser")
                    }
                    assertThat(steps[1]).satisfies { step ->
                        assertThat(step.value).isEqualTo("some Gherkin text")
                    }
                    assertThat(steps[2]).satisfies { step ->
                        assertThat(step.value).isEqualTo("I test the Gherkin parser")
                    }
                    assertThat(steps[3]).satisfies { step ->
                        assertThat(step.value).isEqualTo("I get a valid Document containing the expected information")
                    }
                    assertThat(steps[4]).satisfies { step ->
                        assertThat(step.value).isEqualTo("the Document is not modified")
                    }
                }
            }
        }
    }
}
