package org.livingdoc.jvm.scenario

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.scenario.matching.StepTemplate

internal class ScenarioFixtureModelTest {

    private lateinit var sut: ScenarioFixtureModel
    @BeforeEach
    fun setup() {
        val context = mockk<FixtureContext>()
        every {
            context.fixtureClass
        }.returns(CalculatorFixture::class)
        sut = ScenarioFixtureModel(context)
    }

    @Test
    fun getStepFunctions() {
        assertThat(sut.stepFunctions).hasSize(3)
    }

    @Test
    fun getStepTemplates() {
        assertThat(sut.stepTemplates).hasSize(3)
    }

    @Test
    fun getMatchingFunction() {
        val stepTemplate = StepTemplate.parse("a calculator")

        assertThat(sut.stepTemplateToMethod).hasEntrySatisfying(stepTemplate) {
            assertThat(it).isEqualTo(CalculatorFixture::setup)
        }
    }
}
