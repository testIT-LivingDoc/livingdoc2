package org.livingdoc.engine.execution.examples.scenarios.model

import org.livingdoc.engine.execution.Result

data class ScenarioResult(
        val steps: List<StepResult>,
        var result: Result = Result.Unknown
) {

    companion object {
        fun from(scenario: Scenario): ScenarioResult {
            val stepResults = scenario.steps.map { step -> StepResult(step.value) }
            return ScenarioResult(stepResults)
        }
    }

}