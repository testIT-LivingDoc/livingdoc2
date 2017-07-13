package org.livingdoc.engine.execution.examples.scenarios.matching

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test

class ScenarioStepMatcherTest {

    @Test
    fun `matches step against single template`() {
        val template = StepTemplate.parse("User {username} has entered the building.")
        val cut = ScenarioStepMatcher(arrayListOf(template))
        val result = cut.match("User Peter has entered the building.")
        assertThat(result).isEqualTo(ScenarioStepMatcher.MatchingResult(template, mapOf("username" to "Peter")))
    }

    @Test
    fun `throws when no StepTemplate matches the given step`() {
        val template = StepTemplate.parse("User {username} has entered the building.")
        val cut = ScenarioStepMatcher(arrayListOf(template))
        assertThatExceptionOfType(NoMatchingStepTemplate::class.java).isThrownBy {
            cut.match("This step does not match the provided template at all.")
        }
    }

    @Test
    fun `identifies one StepTemplate matching a given scenario step`() {
        val stepTemplates = arrayListOf(
                "the user '{username}' is logged into the shop",
                "she adds '{movie}' to her shopping cart",
                "she removes '{movie}' from her shopping cart",
                "her shopping cart contains {movies}"
        ).map { StepTemplate.parse(it) }.toList()
        val bestMatch = stepTemplates.first()
        val cut = ScenarioStepMatcher(stepTemplates)

        val result = cut.match("the user 'Paul' is logged into the shop")

        assertThat(result).isEqualTo(ScenarioStepMatcher.MatchingResult(bestMatch, mapOf("username" to "Paul")))
    }

}

