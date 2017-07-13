package org.livingdoc.engine.execution.examples.scenarios.matching

class NoMatchingStepTemplate(msg: String) : RuntimeException(msg) {}

internal class ScenarioStepMatcher(val stepTemplates: List<StepTemplate>) {

    data class MatchingResult(val template: StepTemplate,
                              val variables: Map<String, String>)

    fun match(step: String): MatchingResult {
        val bestAlignment = stepTemplates
                .map { it.alignWith(step, maxCostOfAlignment = 15) }
                .minBy { it.totalCost }
        if (bestAlignment == null || bestAlignment.isMisaligned()) {
            throw NoMatchingStepTemplate("No matching template!")
        }
        return MatchingResult(bestAlignment.stepTemplate, bestAlignment.variables)
    }

}