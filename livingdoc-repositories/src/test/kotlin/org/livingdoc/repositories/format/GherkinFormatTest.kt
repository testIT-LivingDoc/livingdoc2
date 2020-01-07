package org.livingdoc.repositories.format

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.repositories.DocumentFormat
import org.livingdoc.repositories.model.scenario.Scenario

internal class GherkinFormatTest {
    private val cut: DocumentFormat = GherkinFormat()

    @Test
    fun `cannot handle html files`() {
        assertThat(cut.canHandle("html")).isFalse()
    }

    @Test
    fun `cannot handle markdown files`() {
        assertThat(cut.canHandle("md")).isFalse()
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
                    assertThat(steps).hasSize(4)
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
                }
            }
        }
    }
}
