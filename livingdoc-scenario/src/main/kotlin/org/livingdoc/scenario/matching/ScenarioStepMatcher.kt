package org.livingdoc.scenario.matching

// maybe refactor this exception to yield more information
class NoMatchingStepTemplate(msg: String) : RuntimeException(msg)

class ScenarioStepMatcher(private val stepTemplates: List<StepTemplate>) {

    data class MatchingResult(
        val template: StepTemplate,
        val variableToValue: Map<String, String>
    )

    fun match(step: String): MatchingResult {

        if (stepTemplates.isEmpty())
            throw NoMatchingStepTemplate("StepTemplate list must not be empty")

        val bestFit = stepTemplates
            .map { it.alignWith(step, maxLevelOfStemming = 4.0f) }
            .sortedWith(compareBy({ it.totalCost.first }, { it.totalCost.second }))
            .first()

        if (bestFit.isMisaligned()) {
            throw NoMatchingStepTemplate("No matching template!")
        }
        return MatchingResult(bestFit.stepTemplate, bestFit.variables)
    }
}
