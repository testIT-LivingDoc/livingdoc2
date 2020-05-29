package org.livingdoc.engine.execution.examples.scenarios

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.livingdoc.repositories.model.TestDataDescription
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.repositories.model.scenario.Step
import org.livingdoc.results.Status

internal class ScenarioNoFixtureExecutionTest {

    @Test
    fun execute() {
        val step1 = Step("when the customer scans a banana for 49 cents")
        val step2 = Step("and an apple for 39 cents")
        val step3 = Step("when the customer checks out, the total sum is 88")
        val steps = listOf(step1, step2, step3)

        val scenarioMock = Scenario(
            steps,
            TestDataDescription("MANUAL Test1", true, "")
        )

        val cut = ScenarioNoFixture()
        val result = cut.execute(scenarioMock).status
        Assertions.assertThat(result).isInstanceOf(Status.Manual::class.java)
    }
}
