package org.livingdoc.engine.executor.scenario

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class AlignmentTest {

    @Test
    fun `aligns StepTemplate with no variables with perfectly matching string`() {
        val alignment = align("Elvis has left the building.", "Elvis has left the building.")
        assertThat(alignment.distance).isEqualTo(0)
    }

    @Test
    fun `aligns StepTemplate with no variables with similar string`() {
        val alignment = align("Elvis has left the building.", "Peter has left the building.")
        assertThat(alignment.distance).isEqualTo(5)
        assertThat(alignment.alignedStrings).isEqualTo(Pair(
                "Elvis has left the building.",
                "Peter has left the building."
        ))
    }

    @Test
    fun `aligns empty string`() {
        val alignment = align("Elvis likes pizza.", "")
        assertThat(alignment.distance).isEqualTo(18)
        assertThat(alignment.alignedStrings).isEqualTo(Pair(
                "Elvis likes pizza.",
                "------------------"))
    }

    @Test
    fun `aligns StepTemplate with single variable with perfectly matching string`() {
        val alignment = align("User {username} has entered the building.", "User Peter has entered the building.")
        assertThat(alignment.distance).isEqualTo(0)
        assertThat(alignment.variables).isEqualTo(mapOf("username" to "Peter"))
        assertThat(alignment.alignedStrings).isEqualTo(Pair(
                "User XXXXX has entered the building.",
                "User Peter has entered the building."
        ))
    }

    @Test
    fun `aligns StepTemplate with single variable and slightly different text fragment`() {
        val alignment = align("User {user} has entered the building.", "A user Peter has left the building.")
        assertThat(alignment.distance).isEqualTo(9)
        assertThat(alignment.variables).isEqualTo(mapOf("user" to "Peter"))
        assertThat(alignment.alignedStrings).isEqualTo(Pair(
                "--User XXXXX has entered the building.",
                "A user Peter has --le-ft the building."
        ))
    }

    @Test
    fun `aligns StepTemplate starting with a variable`() {
        val alignment = align("{username} has entered the building.", "Peter has left the building.")
        assertThat(alignment.distance).isEqualTo(6)
        assertThat(alignment.variables).isEqualTo(mapOf("username" to "Peter"))
        assertThat(alignment.alignedStrings).isEqualTo(Pair(
                "XXXXX has entered the building.",
                "Peter has --le-ft the building."
        ))
    }

    @Test
    fun `aligns StepTemplate with multiple variables`() {
        val alignment = align("{username} has {action} the {object}.", "Peter has left the building.")
        assertThat(alignment.distance).isEqualTo(0)
        assertThat(alignment.variables).isEqualTo(mapOf(
                "username" to "Peter",
                "action" to "left",
                "object" to "building"))
        assertThat(alignment.alignedStrings).isEqualTo(Pair(
                "XXXXX has XXXX the XXXXXXXX.",
                "Peter has left the building."
        ))
    }

    @Test
    fun `aligns optimally if a single variable remains empty`() {
        val alignment = align("My name is {username}.", "My name is .")
        assertThat(alignment.distance).isEqualTo(0)
        assertThat(alignment.variables).isEqualTo(mapOf("username" to ""))
        assertThat(alignment.alignedStrings).isEqualTo(Pair(
                "My name is X.",
                "My name is -."
        ))
    }

    @Test
    fun `is misaligned when distance becomes too large`() {
        val alignment = align(
                "Elvis left the building and this is a really long sentence that doesn't align with the next one at all.",
                "Peter likes pizza.")
        assertThat(alignment.isMisaligned()).isEqualTo(true)
        assertThat(alignment.distance).isEqualTo(21)
        assertThat(alignment.variables).isEmpty()
        assertThat(alignment.alignedStrings).isEqualTo(Pair(
                "Elvis left the building and this is a really long sentence that doesn't align with the next one at all.",
                "Peter likes pizza."
        ))
    }

    private fun align(templateString: String, step: String): Alignment {
        return Alignment(StepTemplate.parse(templateString), step, maxDistance = 20)
    }

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

