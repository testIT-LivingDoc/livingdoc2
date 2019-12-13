package org.livingdoc.engine.execution.examples.scenarios.matching

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class StemmerHandlerTest {
    val string: String = "hello world"

    @Test
    fun `test cut`() {
        Assertions.assertThat(StemmerHandler.cutLast(string).toString())
            .isEqualTo("hello world")
    }

    @Test
    fun `test cut with blank in end`() {
        Assertions.assertThat(StemmerHandler.cutLast(string + " ").toString())
            .isEqualTo("hello world")
    }

    @Test
    fun `stem on unchanged string`() {
        Assertions.assertThat(StemmerHandler.stemWords(string))
            .isEqualTo("hello world")
    }

    @Test
    fun `stemming tests words part1`() {
        Assertions.assertThat(StemmerHandler.stemWords("deny"))
            .isEqualTo("deni")
        Assertions.assertThat(StemmerHandler.stemWords("declining"))
            .isEqualTo("declin")
        Assertions.assertThat(StemmerHandler.stemWords("diversity"))
            .isEqualTo("diversit")
        Assertions.assertThat(StemmerHandler.stemWords("divers"))
            .isEqualTo("diver")
        Assertions.assertThat(StemmerHandler.stemWords("dental"))
            .isEqualTo("dental")
    }
    @Test
    fun `stemming tests words part2`() {
        Assertions.assertThat(StemmerHandler.stemWords("caresses"))
            .isEqualTo("caress")
        Assertions.assertThat(StemmerHandler.stemWords("ponies"))
            .isEqualTo("poni")
        Assertions.assertThat(StemmerHandler.stemWords("ties"))
            .isEqualTo("ti")
        Assertions.assertThat(StemmerHandler.stemWords("caress"))
            .isEqualTo("caress")
        Assertions.assertThat(StemmerHandler.stemWords("cats"))
            .isEqualTo("cat")

        Assertions.assertThat(StemmerHandler.stemWords("feed"))
            .isEqualTo("feed")
        Assertions.assertThat(StemmerHandler.stemWords("agreed"))
            .isEqualTo("agre")
        Assertions.assertThat(StemmerHandler.stemWords("disabled"))
            .isEqualTo("disabl")

        Assertions.assertThat(StemmerHandler.stemWords("matting"))
            .isEqualTo("mat")
        Assertions.assertThat(StemmerHandler.stemWords("mating"))
            .isEqualTo("mate")
        Assertions.assertThat(StemmerHandler.stemWords("meeting"))
            .isEqualTo("meet")
        Assertions.assertThat(StemmerHandler.stemWords("milling"))
            .isEqualTo("mill")
        Assertions.assertThat(StemmerHandler.stemWords("messing"))
            .isEqualTo("mess")

        Assertions.assertThat(StemmerHandler.stemWords("meetings"))
            .isEqualTo("meet")
    }
    @Test
    fun `stemming tests words part3`() {
        Assertions.assertThat(StemmerHandler.stemWords("mappization"))
            .isEqualTo("mappiz")
        Assertions.assertThat(StemmerHandler.stemWords("sensational"))
            .isEqualTo("sensat")
        Assertions.assertThat(StemmerHandler.stemWords("grenci"))
            .isEqualTo("grenci")
        Assertions.assertThat(StemmerHandler.stemWords("granci"))
            .isEqualTo("granci")
        Assertions.assertThat(StemmerHandler.stemWords("neutralizer"))
            .isEqualTo("neutral")
        Assertions.assertThat(StemmerHandler.stemWords("betional"))
            .isEqualTo("betional")

        Assertions.assertThat(StemmerHandler.stemWords("doubli"))
            .isEqualTo("doubli")
        Assertions.assertThat(StemmerHandler.stemWords("dalli"))
            .isEqualTo("dalli")
        Assertions.assertThat(StemmerHandler.stemWords("rentli"))
            .isEqualTo("rentli")
        Assertions.assertThat(StemmerHandler.stemWords("reli"))
            .isEqualTo("reli")
        Assertions.assertThat(StemmerHandler.stemWords("bousli"))
            .isEqualTo("bousli")
        Assertions.assertThat(StemmerHandler.stemWords("liberation"))
            .isEqualTo("liber")
        Assertions.assertThat(StemmerHandler.stemWords("grundization"))
            .isEqualTo("grundiz")
        Assertions.assertThat(StemmerHandler.stemWords("senator"))
            .isEqualTo("senat")
        Assertions.assertThat(StemmerHandler.stemWords("malism"))
            .isEqualTo("malism")
        Assertions.assertThat(StemmerHandler.stemWords("liveness"))
            .isEqualTo("live")
        Assertions.assertThat(StemmerHandler.stemWords("fulness"))
            .isEqualTo("ful")
        Assertions.assertThat(StemmerHandler.stemWords("gousness"))
            .isEqualTo("gous")
    }

    @Test
    fun `stemming test words part4`() {
        Assertions.assertThat(StemmerHandler.stemWords("taliti"))
            .isEqualTo("taliti")
        Assertions.assertThat(StemmerHandler.stemWords("leiviti"))
            .isEqualTo("leiviti")

        Assertions.assertThat(StemmerHandler.stemWords("enbiliti"))
            .isEqualTo("enbl")
        Assertions.assertThat(StemmerHandler.stemWords("physiologi"))
            .isEqualTo("physiolog")
        Assertions.assertThat(StemmerHandler.stemWords("delicate"))
            .isEqualTo("delic")
        Assertions.assertThat(StemmerHandler.stemWords("decorative"))
            .isEqualTo("decor")
        Assertions.assertThat(StemmerHandler.stemWords("meiciti"))
            .isEqualTo("meiciti")
        Assertions.assertThat(StemmerHandler.stemWords("logical"))
            .isEqualTo("logic")
        Assertions.assertThat(StemmerHandler.stemWords("insightful"))
            .isEqualTo("insight")
        Assertions.assertThat(StemmerHandler.stemWords("clearance"))
            .isEqualTo("clearanc")
        Assertions.assertThat(StemmerHandler.stemWords("scientific"))
            .isEqualTo("scientific")
        Assertions.assertThat(StemmerHandler.stemWords("reliable"))
            .isEqualTo("reliabl")
        Assertions.assertThat(StemmerHandler.stemWords("hence"))
            .isEqualTo("henc")
        Assertions.assertThat(StemmerHandler.stemWords("ant"))
            .isEqualTo("ant")
    }
}
