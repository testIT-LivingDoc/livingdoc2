package org.livingdoc.engine.execution.examples.scenarios.model

import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.TestDataResult
import org.livingdoc.repositories.model.scenario.Scenario

data class ScenarioResult(
    val steps: List<StepResult>,
    var status: Status = Status.Unknown
) : TestDataResult {

    companion object {
        fun from(scenario: Scenario): ScenarioResult {
            val stepResults = scenario.steps.map { (value) -> StepResult(value) }
            return ScenarioResult(stepResults)
        }
    }
}
