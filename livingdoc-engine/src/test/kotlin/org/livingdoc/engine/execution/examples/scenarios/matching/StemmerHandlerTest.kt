package org.livingdoc.engine.execution.examples.scenarios.matching

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class StemmerHandlerTest {
    val string: String = "hello world git"

    @Test
    fun `test cut`() {
        Assertions.assertThat(StemmerHandler.cutLast(string).toString())
            .isEqualTo("hello world git")
    }

    @Test
    fun `test cut with blank in end`() {
        Assertions.assertThat(StemmerHandler.cutLast(string + " ").toString())
            .isEqualTo("hello world git")
    }

    @Test
    fun `stemWords`() {
        Assertions.assertThat(StemmerHandler.stemWords(string).first)
            .isEqualTo("hello world git")
    }

    @Test
    fun `stemming`() {
        Assertions.assertThat(StemmerHandler.stemWords("deny").first)
            .isEqualTo("deni")
        Assertions.assertThat(StemmerHandler.stemWords("declining").first)
            .isEqualTo("declin")
        Assertions.assertThat(StemmerHandler.stemWords("diversity").first)
            .isEqualTo("diversit")
        Assertions.assertThat(StemmerHandler.stemWords("divers").first)
            .isEqualTo("diver")
        Assertions.assertThat(StemmerHandler.stemWords("dental").first)
            .isEqualTo("dental")
    }
}
