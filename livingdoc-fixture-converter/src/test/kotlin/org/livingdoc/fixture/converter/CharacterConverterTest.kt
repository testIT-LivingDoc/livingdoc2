package org.livingdoc.fixture.converter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.livingdoc.fixture.api.converter.ConversionException


internal class CharacterConverterTest {

    val cut = CharacterConverter()

    @ParameterizedTest(name = "\"{0}\"")
    @ValueSource(strings = arrayOf(" ", "\t", "a", "z", "0", "9", "-", "$", "|"))
    fun `any char value can be converted`(value: String) {
        assertThat(cut.convert(value)).isNotNull()
    }

    @org.junit.jupiter.api.Test
    fun `empty string is not a valid char`() {
        assertThrows(ConversionException::class.java, {
            cut.convert("")
        })
    }

    @org.junit.jupiter.api.Test
    fun `one character string is a valid char`() {
        assertThat(cut.convert("a")).isEqualTo('a')
    }

    @org.junit.jupiter.api.Test
    fun `two character string is not a valid char`() {
        assertThrows(ConversionException::class.java, {
            cut.convert("ab")
        })
    }

    @Test
    fun `converter can converted to Kotlin Char`() {
        assertThat(cut.canConvertTo(Char::class.java)).isTrue()
    }

}
