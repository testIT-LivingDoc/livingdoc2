package org.livingdoc.scenario.matching

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchingFunctinsTest {
    fun valueProvider(): Stream<Arguments> {
        return Stream.of(
            Arguments.of("Eating an apple", "Eating a apple", true),
            Arguments.of("an an an", "a a a", true),
            Arguments.of("", "", true),
            Arguments.of("hello", "hello", true),
            Arguments.of("anb", "anb", true),
            Arguments.of("ant ant ant", "ant ant ant", true),
            Arguments.of("+", "\\+", false),
            Arguments.of("\\", "\\\\", false),
            Arguments.of("", "", false),
            Arguments.of("?", "\\?", false),
            Arguments.of("anb", "anb", false),
            Arguments.of(".", "\\.", false)
        )
    }

    @ParameterizedTest()
    @MethodSource("valueProvider")
    fun `test filter string`(value: String, output: String, isStep: Boolean) {
        Assertions.assertThat(MatchingFunctions.filterString(value, isStep)).isEqualTo(output)
    }

    fun varLengthProvider(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                mapOf(
                    "variablename" to "value1",
                    "variablename2" to "value 2 value 1"
                ),
                10.5f
            ),
            Arguments.of(
                mapOf(
                    "variablename" to "value 1 to task 2",
                    "variablename2" to "value 2 value 1"
                ),
                16.0f
            ),
            Arguments.of(
                mapOf(
                    "variablename" to "value1",
                    "variablename2" to "value2"
                ),
                6.0f
            )
        )
    }

    @ParameterizedTest()
    @MethodSource("varLengthProvider")
    fun `test length consideration`(mapValues: Map<String, String>, output: Float) {
        Assertions.assertThat(
            MatchingFunctions.considerVarLength(
                mapValues
            )
        ).isEqualTo(output)
    }

    fun varvalues(): Stream<Arguments> {
        return Stream.of(
            Arguments.of("hello ", "hello"),
            Arguments.of(" hello", "hello"),
            Arguments.of(" hello ", " hello")
        )
    }

    @ParameterizedTest()
    @MethodSource("varvalues")
    fun `test valuePostCalc`(value: String, output: String) {
        Assertions.assertThat(
            MatchingFunctions.variablevaluePostcalc(
                value
            )
        ).isEqualTo(output)
    }

    @Test
    fun `test for var`() {
        Assertions.assertThat(MatchingFunctions.checkIfVar("hello")).isFalse()
        Assertions.assertThat(MatchingFunctions.checkIfVar("{hello}")).isTrue()
        // case cannot occur, brackets cannot close without opening
        // Assertions.assertThat(MatchingFunctions.checkIfVar("}hello{")).isTrue()
    }

    fun templates(): Stream<Arguments> {
        return Stream.of(
            Arguments.of("string string {string} string", "string string (.*\\s|)string", listOf("string")),
            Arguments.of("string {string} string string", "string (.*\\s|)string string", listOf("string")),
            Arguments.of("string {string}", "string(\\s.*|)", listOf("string")),
            Arguments.of("{string}", "(.*)", listOf("string")),
            Arguments.of("string{string}string", "string(.*)string", listOf("string")),
            Arguments.of("string string{string}string string string", "string string(.*)string string string", listOf("string")),
            Arguments.of("string string{string}string string", "string string(.*)string string", listOf("string")),
            Arguments.of("string string{string}string string string", "string string(.*)string string string", listOf("string")),
            Arguments.of("string{string}string string string", "string(.*)string string string", listOf("string")),
            Arguments.of("{string} string string", "(.*\\s|)string string", listOf("string")),
            Arguments.of("{string} string {string}", "(.*\\s|)string(\\s.*|)", listOf("string", "string"))
        )
    }

    @ParameterizedTest()
    @MethodSource("templates")
    fun `test templates`(value: String, regexString: String, variablesMap: List<String>) {
        val output = Pair(regexString, variablesMap)
        Assertions.assertThat(
            MatchingFunctions.templateStepToRegexString(
                value
            )
        ).isEqualTo(output)
    }
}
