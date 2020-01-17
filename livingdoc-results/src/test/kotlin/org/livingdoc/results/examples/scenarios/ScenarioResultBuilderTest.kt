package org.livingdoc.results.examples.scenarios

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.repositories.model.scenario.Step
import org.livingdoc.results.Status

internal class ScenarioResultBuilderTest {

    private val scenarioNoSteps = Scenario(listOf<Step>())
    private val scenarioWithSteps = Scenario(listOf(Step("a"), Step("b")))
    private val fixtureClass: Class<*> = FixtureClass::class.java

    @Test
    fun `test scenario with no steps`() {
        val result = ScenarioResult.Builder()
            .withScenario(scenarioNoSteps)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)
            .build()

        assertThat(result.scenario).isEqualTo(scenarioNoSteps)
        assertThat(result.fixtureSource).isEqualTo(fixtureClass)
        assertThat(result.status).isEqualTo(Status.Executed)
    }

    @Test
    fun `test scenario with steps`() {
        val result = ScenarioResult.Builder()
            .withScenario(scenarioWithSteps)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)
            .withStep(StepResult.Builder().withValue("a").withStatus(Status.Executed).build())
            .withStep(StepResult.Builder().withValue("b").withStatus(Status.Executed).build())
            .build()

        assertThat(result.scenario).isEqualTo(scenarioWithSteps)
        assertThat(result.steps).hasSize(2)
        assertThat(result.steps[0].status).isEqualTo(Status.Executed)
        assertThat(result.steps[1].status).isEqualTo(Status.Executed)
    }

    @Test
    fun `test scenario with missing steps`() {
        val builder = ScenarioResult.Builder()
            .withScenario(scenarioWithSteps)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)
            .withStep(StepResult.Builder().withValue("a").withStatus(Status.Executed).build())

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test scenario with too many steps`() {
        val builder = ScenarioResult.Builder()
            .withScenario(scenarioWithSteps)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)
            .withStep(StepResult.Builder().withValue("a").withStatus(Status.Executed).build())
            .withStep(StepResult.Builder().withValue("b").withStatus(Status.Executed).build())
            .withStep(StepResult.Builder().withValue("c").withStatus(Status.Executed).build())

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test scenario with wrong steps`() {
        val builder = ScenarioResult.Builder()
            .withScenario(scenarioWithSteps)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)
            .withStep(StepResult.Builder().withValue("a").withStatus(Status.Executed).build())
            .withStep(StepResult.Builder().withValue("c").withStatus(Status.Executed).build())

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test with unassigned skipped`() {
        val result = ScenarioResult.Builder()
            .withScenario(scenarioWithSteps)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)
            .withUnassignedSkipped()
            .build()

        assertThat(result.scenario).isEqualTo(scenarioWithSteps)
        assertThat(result.fixtureSource).isEqualTo(fixtureClass)
        assertThat(result.status).isEqualTo(Status.Executed)
        assertThat(result.steps).hasSize(2)
        assertThat(result.steps[0].status).isEqualTo(Status.Skipped)
        assertThat(result.steps[1].status).isEqualTo(Status.Skipped)
    }

    @Test
    fun `test scenario without scenario`() {
        val builder = ScenarioResult.Builder()
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test scenario without status`() {
        val builder = ScenarioResult.Builder()
            .withScenario(scenarioWithSteps)
            .withFixtureSource(fixtureClass)
            .withStep(StepResult.Builder().withValue("a").withStatus(Status.Executed).build())
            .withStep(StepResult.Builder().withValue("b").withStatus(Status.Executed).build())

        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `test finalized builder`() {
        val builder = ScenarioResult.Builder()
            .withScenario(scenarioWithSteps)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Executed)
            .withStep(StepResult.Builder().withValue("a").withStatus(Status.Executed).build())
            .withStep(StepResult.Builder().withValue("b").withStatus(Status.Executed).build())
        builder.build()

        assertThrows<IllegalStateException> {
            builder.withScenario(scenarioWithSteps)
        }

        assertThrows<IllegalStateException> {
            builder.withFixtureSource(fixtureClass)
        }

        assertThrows<IllegalStateException> {
            builder.withStatus(Status.Skipped)
        }

        assertThrows<IllegalStateException> {
            builder.withStep(StepResult.Builder().withValue("c").withStatus(Status.Executed).build())
        }
    }

    @Test
    fun `test auto generate step results for manual test`() {
        val result = ScenarioResult.Builder()
            .withScenario(scenarioWithSteps)
            .withFixtureSource(fixtureClass)
            .withStatus(Status.Manual)
            .build()

        assertThat(result.status).isEqualTo(Status.Manual)
        assertThat(result.steps[0].status).isEqualTo(Status.Manual)
        assertThat(result.steps[1].status).isEqualTo(Status.Manual)
    }

    class FixtureClass
}
