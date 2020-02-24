package org.livingdoc.repositories.format

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.livingdoc.repositories.DocumentFormat
import org.livingdoc.repositories.model.scenario.Scenario
import kotlin.random.Random
import kotlin.random.nextInt

internal class GherkinFormatTest {
    private val cut: DocumentFormat = GherkinFormat()

    @ParameterizedTest
    @ValueSource(strings = ["txt", "html", "md"])
    @MethodSource("generateRandomStrings")
    fun `cannot handle other files`(format: String) {
        assertThat(cut.canHandle(format)).isFalse()
    }

    @Test
    fun `can handle feature files`() {
        assertThat(cut.canHandle("feature")).isTrue()
    }

    @Test
    fun `can parse empty stream`() {
        val document = cut.parse("".byteInputStream())
        assertThat(document.elements).isEmpty()
    }

    @Test
    fun `can parse simple scenario`() {
        val document = cut.parse(simpleGherkin())

        assertThat(document.elements).hasOnlyOneElementSatisfying { element ->
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

    @Test
    fun `can parse multiple scenarios`() {
        val document = cut.parse(multipleScenarioGherkin())

        assertThat(document.elements).hasSize(2)
        assertThat(document.elements[0]).satisfies { testData ->
            assertThat(testData.description).satisfies { description ->
                assertThat(description.name).isEqualTo("Test Scenario 1")
                assertThat(description.isManual).isFalse()
            }

            assertThat(testData).isInstanceOfSatisfying(Scenario::class.java) { scenario ->
                assertThat(scenario.steps).hasOnlyOneElementSatisfying { step ->
                    assertThat(step.value).isEqualTo("I test the Gherkin parser")
                }
            }
        }
        assertThat(document.elements[1]).satisfies { testData ->
            assertThat(testData.description).satisfies { description ->
                assertThat(description.name).isEqualTo("Test Scenario 2")
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
    fun `can parse multiple steps in a scenario`() {
        val document = cut.parse(multipleStepScenarioGherkin())

        assertThat(document.elements).hasOnlyOneElementSatisfying { testData ->
            assertThat(testData.description).satisfies { description ->
                assertThat(description.name).isEqualTo("Test Scenario 1")
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

    @Test
    fun `can parse multiple steps in a german scenario`() {
        val document = cut.parse(multipleStepScenarioInGermanGherkin())

        assertThat(document.elements).hasOnlyOneElementSatisfying { testData ->
            assertThat(testData.description).satisfies { description ->
                assertThat(description.name).isEqualTo("Test eines Szenarios")
                assertThat(description.isManual).isFalse()
            }

            assertThat(testData).isInstanceOfSatisfying(Scenario::class.java) { scenario ->
                assertThat(scenario.steps).satisfies { steps ->
                    assertThat(steps).hasSize(5)
                    assertThat(steps[0]).satisfies { step ->
                        assertThat(step.value).isEqualTo("ein funktionierender Gherkin-Parser")
                    }
                    assertThat(steps[1]).satisfies { step ->
                        assertThat(step.value).isEqualTo("etwas Gherkin-Text")
                    }
                    assertThat(steps[2]).satisfies { step ->
                        assertThat(step.value).isEqualTo("ich den Parser teste")
                    }
                    assertThat(steps[3]).satisfies { step ->
                        assertThat(step.value).isEqualTo(
                            "bekomme ich ein korrektes Dokument mit den erwarteten " +
                                    "Informationen"
                        )
                    }
                    assertThat(steps[4]).satisfies { step ->
                        assertThat(step.value).isEqualTo("das Dokument ist nicht modifiziert")
                    }
                }
            }
        }
    }

    @Test
    fun `can parse multiple steps in a emoji scenario`() {
        val document = cut.parse(multipleStepScenarioInEmojiGherkin())

        assertThat(document.elements).hasOnlyOneElementSatisfying { testData ->
            assertThat(testData.description).satisfies { description ->
                assertThat(description.name).isEqualTo("Test eines Szenarios")
                assertThat(description.isManual).isFalse()
            }

            assertThat(testData).isInstanceOfSatisfying(Scenario::class.java) { scenario ->
                assertThat(scenario.steps).satisfies { steps ->
                    assertThat(steps).hasSize(5)
                    assertThat(steps[0]).satisfies { step ->
                        assertThat(step.value).isEqualTo("ein funktionierender Gherkin-Parser")
                    }
                    assertThat(steps[1]).satisfies { step ->
                        assertThat(step.value).isEqualTo("etwas Gherkin-Text")
                    }
                    assertThat(steps[2]).satisfies { step ->
                        assertThat(step.value).isEqualTo("ich den Parser teste")
                    }
                    assertThat(steps[3]).satisfies { step ->
                        assertThat(step.value).isEqualTo(
                            "bekomme ich ein korrektes Dokument mit den erwarteten " +
                                    "Informationen"
                        )
                    }
                    assertThat(steps[4]).satisfies { step ->
                        assertThat(step.value).isEqualTo("das Dokument ist nicht modifiziert")
                    }
                }
            }
        }
    }

    @Test
    fun `can parse scenario outline`() {
        val document = cut.parse(scenarioOutlineGherkin())

        assertThat(document.elements).satisfies { elements ->
            assertThat(elements).hasSize(2)
            assertThat(elements[0]).isInstanceOfSatisfying(Scenario::class.java) { scenario ->
                assertThat(scenario.steps).satisfies { steps ->
                    assertThat(steps).hasSize(3)
                    assertThat(steps[0]).satisfies { step ->
                        assertThat(step.value).isEqualTo("there are 12 cucumbers")
                    }
                    assertThat(steps[1]).satisfies { step ->
                        assertThat(step.value).isEqualTo("I eat 5 cucumbers")
                    }
                    assertThat(steps[2]).satisfies { step ->
                        assertThat(step.value).isEqualTo("I should have 7 cucumbers")
                    }
                }
                assertThat(scenario.description.name).isEqualTo("eating")
            }
            assertThat(elements[1]).isInstanceOfSatisfying(Scenario::class.java) { scenario ->
                assertThat(scenario.steps).satisfies { steps ->
                    assertThat(steps).hasSize(3)
                    assertThat(steps[0]).satisfies { step ->
                        assertThat(step.value).isEqualTo("there are 20 cucumbers")
                    }
                    assertThat(steps[1]).satisfies { step ->
                        assertThat(step.value).isEqualTo("I eat 5 cucumbers")
                    }
                    assertThat(steps[2]).satisfies { step ->
                        assertThat(step.value).isEqualTo("I should have 15 cucumbers")
                    }
                }
                assertThat(scenario.description.name).isEqualTo("eating")
            }
        }
    }

    @Test
    fun `can parse doc string scenario`() {
        val document = cut.parse(
            docStringGherkin()
        )

        assertThat(document.elements).hasOnlyOneElementSatisfying { element ->
            assertThat(element.description).satisfies { description ->
                assertThat(description.name).isEqualTo("Test Scenario")
                assertThat(description.isManual).isFalse()
            }

            assertThat(element).isInstanceOfSatisfying(Scenario::class.java) { scenario ->
                assertThat(scenario.steps).hasOnlyOneElementSatisfying { step ->
                    assertThat(step.value).isEqualTo(
                        "I test the Gherkin parser with Some Title, Eh?\n" +
                                "===============\n" +
                                "Here is the first paragraph of my blog post. Lorem ipsum dolor sit amet,\n" +
                                "consectetur adipiscing elit."
                    )
                }
            }
        }
    }

    @Test
    fun `can parse advanced doc string scenario`() {
        val document = cut.parse(
            docStringAdvancedGherkin()
        )

        assertThat(document.elements).hasOnlyOneElementSatisfying { element ->
            assertThat(element.description).satisfies { description ->
                assertThat(description.name).isEqualTo("Test Scenario")
                assertThat(description.isManual).isFalse()
            }

            assertThat(element).isInstanceOfSatisfying(Scenario::class.java) { scenario ->
                assertThat(scenario.steps).satisfies { steps ->
                    assertThat(steps).hasSize(2)
                    assertThat(steps[0]).satisfies { step ->
                        assertThat(step.value).isEqualTo(
                            "I test the Gherkin parser with Some Title, Eh?\n" +
                                    "===============\n" +
                                    "Here is the first paragraph of my blog post. Lorem ipsum dolor sit amet,\n" +
                                    "consectetur adipiscing elit."
                        )
                    }
                    assertThat(steps[1]).satisfies { step ->
                        assertThat(step.value).isEqualTo(
                            "I have some text"
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `can parse data table scenario`() {
        val document = cut.parse(
            dataTableGherkin()
        )

        assertThat(document.elements).hasOnlyOneElementSatisfying { element ->
            assertThat(element.description).satisfies { description ->
                assertThat(description.name).isEqualTo("Test Scenario")
                assertThat(description.isManual).isFalse()
            }

            assertThat(element).isInstanceOfSatisfying(Scenario::class.java) { scenario ->
                assertThat(scenario.steps).hasOnlyOneElementSatisfying { step ->
                    assertThat(step.value).isEqualToIgnoringWhitespace(
                        "the following users exist:" +
                                " {\"rows\" : [[\"name\", \"email\", \"twitter\"]," +
                                " [\"Aslak\", \"aslak@cucumber.io\", \"@aslak_hellesoy\"]," +
                                " [\"Julien\", \"julien@cucumber.io\", \"@jbpros\"]," +
                                " [\"Matt\", \"matt@cucumber.io\", \"@mattwynne\"]]}"
                    )
                }
            }
        }
    }

    @Test
    fun `can parse advanced data table scenario`() {
        val document = cut.parse(
            dataTableAdvancedGherkin()
        )

        assertThat(document.elements).hasOnlyOneElementSatisfying { element ->
            assertThat(element.description).satisfies { description ->
                assertThat(description.name).isEqualTo("Test Scenario")
                assertThat(description.isManual).isFalse()
            }

            assertThat(element).isInstanceOfSatisfying(Scenario::class.java) { scenario ->
                assertThat(scenario.steps).satisfies { steps ->
                    assertThat(steps).hasSize(2)
                    assertThat(steps[0]).satisfies { step ->
                        assertThat(step.value).isEqualToIgnoringWhitespace(
                            "the following users exist:" +
                                    " {\"rows\" : [[\"name\", \"email\", \"twitter\"]," +
                                    " [\"Aslak\", \"aslak@cucumber.io\", \"@aslak_hellesoy\"]," +
                                    " [\"Julien\", \"julien@cucumber.io\", \"@jbpros\"]," +
                                    " [\"Matt\", \"matt@cucumber.io\", \"@mattwynne\"]]}"
                        )
                    }
                    assertThat(steps[1]).satisfies { step ->
                        assertThat(step.value).isEqualTo(
                            "I have a list of names"
                        )
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun generateRandomStrings(): List<String> {
            return (0..999).map {
                generateRandomString()
            }
        }

        private fun generateRandomString(): String {
            val length = Random.nextInt(3..10)

            var result: String
            do {
                result = Random.nextBytes(length).toString(Charsets.UTF_8)
            } while (result == "feature")

            return result
        }
    }
}
