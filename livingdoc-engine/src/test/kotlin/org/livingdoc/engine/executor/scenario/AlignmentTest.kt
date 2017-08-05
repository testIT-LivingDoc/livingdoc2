package org.livingdoc.engine.executor.scenario

import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AlignmentTest {

    @Nested inner class `given a StepTemplate without variables` {

        @Test fun `aligns perfectly matching step`() {
            val alignment = align("Elvis has left the building.", "Elvis has left the building.")
            assertThat(alignment).hasNoVariables().hasDistance(0)
        }

        @Test fun `aligns similar step`() {
            val alignment = align("Elvis has left the building.", "Peter has left the building.")
            assertThat(alignment)
                    .hasNoVariables()
                    .hasDistance(5)
                    .alignsAs("Elvis has left the building.", "Peter has left the building.")
        }

        @Test fun `aligns empty string`() {
            val alignment = align("Elvis likes pizza.", "")
            assertThat(alignment)
                    .hasNoVariables()
                    .hasDistance(18)
                    .alignsAs("Elvis likes pizza.", "------------------")
        }
    }


    @Nested inner class `given a StepTemplate with variables` {

        @Test fun `extracts value from perfectly matching step`() {
            val alignment = align("User {username} has entered the building.", "User Peter has entered the building.")
            assertThat(alignment)
                    .hasDistance(0)
                    .hasVariables("username" to "Peter")
                    .alignsAs("User XXXXX has entered the building.", "User Peter has entered the building.")
        }

        @Test fun `extracts value from step with slightly different text fragments`() {
            val alignment = align("User {user} has entered the building.", "A user Peter has left the building.")
            assertThat(alignment)
                    .hasDistance(9)
                    .hasVariables("user" to "Peter")
                    .alignsAs("--User XXXXX has entered the building.", "A user Peter has --le-ft the building.")
        }

        @Test fun `extracts value from step when the variable is the first part of the template`() {
            val alignment = align("{username} has entered the building.", "Peter has left the building.")
            assertThat(alignment)
                    .hasDistance(6)
                    .hasVariables("username" to "Peter")
                    .alignsAs("XXXXX has entered the building.", "Peter has --le-ft the building.")
        }

        @Test fun `extracts all of the values`() {
            val alignment = align("{username} has {action} the {object}.", "Peter has left the building.")
            assertThat(alignment)
                    .hasDistance(0)
                    .hasVariables(
                            "username" to "Peter",
                            "action" to "left",
                            "object" to "building")
                    .alignsAs(
                            "XXXXX has XXXX the XXXXXXXX.",
                            "Peter has left the building.")
        }

        @Test fun `extracts empty string as value from perfectly matching step`() {
            val alignment = align("My name is {username}.", "My name is .")
            assertThat(alignment)
                    .hasDistance(0)
                    .hasVariables("username" to "")
                    .alignsAs(
                            "My name is X.",
                            "My name is -.")
        }
    }


    @Test fun `given no matching StepTemplate, it is misaligned`() {
        val alignment = align(
                "Elvis left the building and this is a really long sentence that doesn't align with the next one at all.",
                "Peter likes pizza.")

        assertThat(alignment)
                .isMisaligned()
                .alignsAs(
                        "Elvis left the building and this is a really long sentence that doesn't align with the next one at all.",
                        "Peter likes pizza.")
                .hasNoVariables()
        Assertions.assertThat(alignment.maxDistance <= alignment.distance).isTrue()
    }


    @Nested inner class `given a StepTemplate with quotation characters` {

        @Test fun `extracts variables from perfectly matching step`() {
            val alignment = alignWithPunctuation("{user} likes '{stuff}'.", "Peter likes 'Pizza'.")
            assertThat(alignment)
                    .alignsAs("XXXXX likes 'XXXXX'.", "Peter likes 'Pizza'.")
                    .hasVariables("user" to "Peter", "stuff" to "Pizza")
        }

        @Test fun `extracts variable with adjacent insertion`() {
            val alignment = alignWithPunctuation("'{user}' likes '{stuff}'.", "'Peter' likes delicious 'Pizza'.")
            assertThat(alignment)
                    .hasVariables("user" to "Peter", "stuff" to "Pizza")
                    .alignsAs("'XXXXX' likes ----------'XXXXX'.", "'Peter' likes delicious 'Pizza'.")
        }

        @Test fun `is misaligned, if a quotation character is missed in the step`() {
            val alignment = alignWithPunctuation(
                    "Peter does not like '{stuff}'.",
                    "Peter does not like missing punctuation marks'.")
            assertThat(alignment).isMisaligned()
        }

        @Test fun `extracted variables do not contain quotation characters`() {
            val alignment = alignWithPunctuation(
                    "Peter does not like '{stuff}'.",
                    "Peter does not like ''unnecessary quotation marks'.")
            Assertions.assertThat(alignment.variables["stuff"]).doesNotContain("'")
            assertThat(alignment)
                    .hasVariables("stuff" to "unnecessary quotation marks")
                    .hasDistance(1) // due to insertion of the unnecessary single quotation mark
        }

        private fun alignWithPunctuation(templateString: String, step: String): Alignment {
            return Alignment(StepTemplate.parse(templateString, quotationCharacters = setOf('\'')), step, maxDistance = 20)
        }
    }


    private fun align(templateString: String, step: String): Alignment {
        return Alignment(StepTemplate.parse(templateString), step, maxDistance = 20)
    }

}


private fun assertThat(actual: Alignment) = AlignmentAssert(actual)

private class AlignmentAssert(actual: Alignment)
    : AbstractAssert<AlignmentAssert, Alignment>(actual, AlignmentAssert::class.java) {

    fun hasDistance(distance: Int): AlignmentAssert {
        if (actual.distance != distance)
            failWithMessage("Expected an editing distance of $distance, but was ${actual.distance}")
        return this
    }

    fun isMisaligned(): AlignmentAssert {
        if (!actual.isMisaligned())
            failWithMessage("Expected alignment to be misaligned, but it was not.")
        return this
    }

    fun alignsAs(template: String, step: String): AlignmentAssert {
        if (actual.alignedStrings != Pair(template, step)) {
            val reason = if (actual.alignedStrings.first != template)
                "the template is aligned differently"
            else if (actual.alignedStrings.second != step)
                "the step is aligned differently"
            else
                "both template and step are aligned differently"

            failWithMessage("Expected alignment\n\t(template) %s\n\t    (step) %s\n" +
                    "to be equal to\n\t(template) %s\n\t    (step) %s\nbut %s.",
                    actual.alignedStrings.first, actual.alignedStrings.second, template, step, reason)
        }
        return this
    }

    fun hasNoVariables(): AlignmentAssert {
        if (!actual.variables.isEmpty())
            failWithMessage("Expected alignment with no variables, but there are:\n%s",
                    formatVariables(actual.variables))
        return this
    }

    fun hasVariables(vararg variables: Pair<String, String>): AlignmentAssert {
        val variablesToValues = variables.toMap()
        val missingVariables = variablesToValues.keys.subtract(actual.variables.keys)
        val unexpectedVariables = actual.variables.keys.subtract(variablesToValues.keys)
        val wrongValues = variablesToValues.keys
                .intersect(actual.variables.keys)
                .filter { variablesToValues[it] != actual.variables[it] }

        if (missingVariables.isNotEmpty() || unexpectedVariables.isNotEmpty() || wrongValues.isNotEmpty()) {
            val reasons = mutableListOf<String>()
            if (missingVariables.isNotEmpty())
                reasons.add("is missing " + missingVariables.joinToString { "'$it'" })
            if (unexpectedVariables.isNotEmpty())
                reasons.add("unexpectedly also yields " + unexpectedVariables.joinToString { "'$it'" })
            if (wrongValues.isNotEmpty())
                wrongValues.forEach {
                    reasons.add("the value of '$it' was '${actual.variables[it]}', not '${variablesToValues[it]}'")
                }

            failWithMessage("Expected alignment yielding the variables\n%s\nto yield\n%s\nbut it %s.",
                    formatVariables(actual.variables),
                    formatVariables(variablesToValues),
                    reasons.joinToString(separator = "\n\tand "))
        }
        return this
    }

    private fun formatVariables(variables: Map<String, String>)
            = variables.asIterable().joinToString(separator = "\n\t", prefix = "\t") { "${it.key} = ${it.value}" }
}


/**
 * Prints the distance matrix `d` for the string `s` to the standard output. Useful for debugging.
 */
fun printDistanceMatrix(s: String, d: DistanceMatrix) {
    print("   ")
    s.forEach { print(String.format("%3c", it)) }
    println()
    for (line in d) {
        line.forEach { print(String.format("%3d", it)) }
        println()
    }
}

