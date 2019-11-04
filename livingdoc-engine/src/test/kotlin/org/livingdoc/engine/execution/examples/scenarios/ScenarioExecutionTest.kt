package org.livingdoc.engine.execution.examples.scenarios

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.livingdoc.engine.execution.Result
import org.livingdoc.engine.mockkJClass
import org.livingdoc.engine.resources.DisabledScenarioDocument
import org.livingdoc.repositories.model.scenario.Scenario

internal class ScenarioExecutionTest {

    @Test
    fun disabledScenarioExecute() {
        val scenarioMock = mockkJClass(Scenario::class.java)
        val fixtureClass = DisabledScenarioDocument.DisabledScenarioFixture::class.java
        val cut = ScenarioExecution(fixtureClass, scenarioMock, null)

        val result = cut.execute().result

        Assertions.assertThat(result).isInstanceOf(Result.Disabled::class.java)
        Assertions.assertThat((result as Result.Disabled).reason).isEqualTo("Disabled ScenarioFixture")
    }
}
