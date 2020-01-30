package org.livingdoc.engine.execution.examples.scenarios.matching

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
class StemmerHandlerTest {
    val string: String = "hello world"

    @Test
    fun `stem on unchanged string`() {
        Assertions.assertThat(StemmerHandler.stemWords(string))
            .isEqualTo("hello world ")
    }

    /**
     * provides values for parameterized test for stemming algorithm
     */
    fun valueProvider(): Stream<Arguments> {
        return Stream.of(
            Arguments.of("deny", "deni "),
            Arguments.of("declining", "declin "),
            Arguments.of("diversity", "diversit "),
            Arguments.of("divers", "diver "),
            Arguments.of("dental", "dental "),
            Arguments.of("caress", "caress "),
            Arguments.of("cats", "cat "),
            Arguments.of("feed", "feed "),
            Arguments.of("agreed", "agre "),
            Arguments.of("disabled", "disabl "),
            Arguments.of("matting", "mat "),
            Arguments.of("mating", "mate "),
            Arguments.of("meeting", "meet "),
            Arguments.of("milling", "mill "),
            Arguments.of("messing", "mess "),
            Arguments.of("meetings", "meet "),
            Arguments.of("mappization", "mappiz "),
            Arguments.of("sensational", "sensat "),
            Arguments.of("grenci", "grenci "),
            Arguments.of("granci", "granci "),
            Arguments.of("neutralizer", "neutral "),
            Arguments.of("betional", "betional "),
            Arguments.of("doubli", "doubli "),
            Arguments.of("dalli", "dalli "),
            Arguments.of("rentli", "rentli "),
            Arguments.of("reli", "reli "),
            Arguments.of("bousli", "bousli "),
            Arguments.of("liberation", "liber "),
            Arguments.of("grundization", "grundiz "),
            Arguments.of("senator", "senat "),
            Arguments.of("malism", "malism "),
            Arguments.of("liveness", "live "),
            Arguments.of("fulness", "ful "),
            Arguments.of("gousness", "gous "),
            Arguments.of("taliti", "taliti "),
            Arguments.of("leiviti", "leiviti "),
            Arguments.of("enbiliti", "enbl "),
            Arguments.of("physiologi", "physiolog "),
            Arguments.of("delicate", "delic "),
            Arguments.of("decorative", "decor "),
            Arguments.of("meiciti", "meiciti "),
            Arguments.of("insightful", "insight "),
            Arguments.of("clearance", "clearanc "),
            Arguments.of("scientific", "scientific "),
            Arguments.of("reliable", "reliabl "),
            Arguments.of("hence", "henc "),
            Arguments.of("ant", "ant ")
            )
    }
    @ParameterizedTest
    @MethodSource("valueProvider")
    fun `stemming tests words`(input: String, expectedValue: String) {
        Assertions.assertThat(StemmerHandler.stemWords(input)).isEqualTo(expectedValue)
    }
}
