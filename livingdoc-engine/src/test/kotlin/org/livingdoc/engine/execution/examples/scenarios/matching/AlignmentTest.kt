package org.livingdoc.engine.execution.examples.scenarios.matching

import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


private val PRINT_DEBUG_OUTPUT = false

class AlignmentTest {

    @Nested inner class `given a StepTemplate without variables` {

        @Test fun `aligns perfectly matching step`() {
            val alignment = align("Elvis has left the building.", "Elvis has left the building.")
            assertThat(alignment)
                    .hasNoVariables()
                    .alignsAs(
                            "Elvis has left the building.",
                            "Elvis has left the building.")
        }

        @Test fun `aligns similar step`() {
            val alignment = align("Elvis has entered the building.", "Peter has left the building.")
            assertThat(alignment)
                    .hasNoVariables()
                    .alignsAs(
                            "Elvis has -entered the building.",
                            "Peter has left---- the building.")
        }

        @Test fun `aligns empty string`() {
            val alignment = align("Elvis likes pizza.", "")
            assertThat(alignment)
                    .hasNoVariables()
                    .alignsAs(
                            "Elvis likes pizza.",
                            "------------------")
        }
    }


    @Nested inner class `given a StepTemplate with variables` {

        @Test fun `extracts value from perfectly matching step`() {
            val alignment = align("User {username} has entered the building.", "User Peter has entered the building.")
            assertThat(alignment).hasVariables("username" to "Peter")
        }

        @Test fun `extracts value from step with slightly different text fragments`() {
            val alignment = align("User {user} has entered the building.", "A user Peter has left the building.")
            assertThat(alignment).hasVariables("user" to "Peter")
        }

        @Test fun `extracts value from step when the variable is the first part of the template`() {
            val alignment = align("{username} has entered the building.", "Peter has left the building.")
            assertThat(alignment).hasVariables("username" to "Peter")
        }

        @Test fun `extracts all of the values`() {
            val alignment = align("{username} has {action} the {object}.", "Peter has left the building.")
            assertThat(alignment).hasVariables(
                    "username" to "Peter",
                    "action" to "left",
                    "object" to "building")
        }

        @Test fun `extracts empty string as value from perfectly matching step`() {
            val alignment = align("My name is {username}.", "My name is .")
            assertThat(alignment)
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
        Assertions.assertThat(alignment.maxCost <= alignment.totalCost).isTrue()
    }


    @Nested inner class `given a StepTemplate with quotation characters` {

        @Test fun `extracts variable from perfectly matching step`() {
            val alignment = alignWithQuotationCharacters("Peter likes '{stuff}'.", "Peter likes 'Pizza'.")
            assertThat(alignment).hasVariables("stuff" to "Pizza")
        }

        @Test fun `extracts variable with adjacent insertion`() {
            val alignment = alignWithQuotationCharacters("'{user}' likes '{stuff}'.", "'Peter' likes delicious 'Pizza'.")
            assertThat(alignment).hasVariables("user" to "Peter", "stuff" to "Pizza")
        }

        @Test fun `extracts from quoted and unquoted variables in the same template`() {
            val alignment = alignWithQuotationCharacters("{user} likes '{stuff}'.", "Peter likes delicious 'Pizza'.")
            assertThat(alignment).hasVariables("user" to "Peter", "stuff" to "Pizza")
        }

        @Test fun `is misaligned, if a quotation character is missing in the step`() {
            val alignment = alignWithQuotationCharacters(
                    "Peter does not like '{stuff}'.",
                    "Peter does not like missing punctuation marks'.")
            assertThat(alignment).isMisaligned()
        }

        @Test fun `extracted variables do not contain quotation characters`() {
            val alignment = alignWithQuotationCharacters(
                    "Peter does not like '{stuff}'.",
                    "Peter does not like ''unnecessary quotation marks'.")
            Assertions.assertThat(alignment.variables["stuff"]).doesNotContain("'")
            assertThat(alignment).hasVariables("stuff" to "unnecessary quotation marks")
        }

        private fun alignWithQuotationCharacters(templateString: String, step: String): Alignment {
            return Alignment(StepTemplate.parse(templateString, quotationCharacters = setOf('\'')), step, maxCost = MAX_DISTANCE)
        }
    }


    private fun align(templateString: String, step: String): Alignment {
        return Alignment(StepTemplate.parse(templateString), step, maxCost = MAX_DISTANCE)
    }

    private val MAX_DISTANCE = 40 // allow for 20 insertions (a bit much, but useful for the examples in this test)
}

private fun assertThat(actual: Alignment): AlignmentAssert {
    if (PRINT_DEBUG_OUTPUT) {
        printDistanceMatrix(actual)
        printAlignment(actual)
    }
    return AlignmentAssert(actual)
}

private class AlignmentAssert(actual: Alignment)
    : AbstractAssert<AlignmentAssert, Alignment>(actual, AlignmentAssert::class.java) {

    fun hasTotalCost(cost: Int): AlignmentAssert {
        if (actual.totalCost != cost)
            failWithMessage("Expected a total cost of $cost, but was ${actual.totalCost}")
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
                reasons.add("it is missing " + missingVariables.joinToString { "'$it'" })
            if (unexpectedVariables.isNotEmpty())
                reasons.add("it unexpectedly also yields " + unexpectedVariables.joinToString { "'$it'" })
            if (wrongValues.isNotEmpty())
                wrongValues.forEach {
                    reasons.add("the value of '$it' was '${actual.variables[it]}', not '${variablesToValues[it]}'")
                }

            val description = if (actual.variables.isEmpty())
                "with no variables "
            else
                String.format("yielding the variables\n%s\n", formatVariables(actual.variables))
            failWithMessage("Expected alignment %sto yield\n%s\nbut %s.",
                    description,
                    formatVariables(variablesToValues),
                    reasons.joinToString(separator = "\n\tand "))
        }
        return this
    }

    private fun formatVariables(variables: Map<String, String>)
            = variables.asIterable().joinToString(separator = "\n\t", prefix = "\t") { "${it.key} = ${it.value}" }
}


/**
 * Prints the distance matrix of the given alignment.
 */
private fun printDistanceMatrix(alignment: Alignment) {
    print("   ")
    alignment.step.forEach { print(String.format("%3c", it)) }
    println()
    for (line in alignment.distanceMatrix) {
        line.forEach { print(String.format("%3d", it)) }
        println()
    }
}

/**
 * Prints the aligned strings of template and step for the given alignment.
 */
private fun printAlignment(alignment: Alignment) {
    println("\t(template) ${alignment.alignedStrings.first}" +
            "\n\t    (step) ${alignment.alignedStrings.second}")
}

