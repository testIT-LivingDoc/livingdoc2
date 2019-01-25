package org.livingdoc.engine.execution.examples.scenarios.model

import org.livingdoc.engine.execution.Result
import org.livingdoc.engine.execution.examples.ExampleResult
import org.livingdoc.repositories.model.scenario.Scenario

data class ScenarioResult(
    val steps: List<StepResult>,
    var result: Result = Result.Unknown
) : ExampleResult {

    companion object {
        fun from(scenario: Scenario): ScenarioResult {
            val stepResults = scenario.steps.map { (value) -> StepResult(value) }
            return ScenarioResult(stepResults)
        }
    }
}
