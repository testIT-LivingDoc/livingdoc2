package org.livingdoc.engine.execution.examples.scenarios.matching

// maybe refactor this exception to yield more information
class NoMatchingStepTemplate(msg: String) : RuntimeException(msg)

internal class ScenarioStepMatcher(private val stepTemplates: List<StepTemplate>) {

    data class MatchingResult(
        val template: StepTemplate,
        val variables: Map<String, String>
    )

    fun match(step: String): MatchingResult {

        // input is a stepTemplate List
        // returns a Matching result: Steptemplate, variables matching

        // start alignemnt
        // find best
        // if none fit throw exception
        // get vars
        // get steptemplate

        val bestFit = stepTemplates
            .map { it.alignWith(step, maxCostOfAlignment = 2) }
            .minBy { it.totalCost }

        if (bestFit == null || bestFit.isMisaligned()) {
            throw NoMatchingStepTemplate("No matching template!")
        }
        bestFit.variables.forEach {
            if (it.value == "")
                throw NoMatchingStepTemplate("No matching template!")
        }

        println(bestFit.stepTemplate.toString())
        println(bestFit.variables)
        return MatchingResult(bestFit.stepTemplate, bestFit.variables)
    }
}
