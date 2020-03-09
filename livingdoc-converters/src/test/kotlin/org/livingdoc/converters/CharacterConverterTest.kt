package org.livingdoc.converters

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.livingdoc.api.conversion.ConversionException

internal class CharacterConverterTest : DefaultTypeConverterContract {

    override val cut = CharacterConverter()

    @ParameterizedTest(name = "\"{0}\"")
    @ValueSource(strings = [" ", "\t", "a", "z", "0", "9", "-", "$", "|"])
    fun `any char value can be converted`(value: String) {
        assertThat(cut.convertValueOnly(value)).isNotNull()
    }

    @Test
    fun `empty string is not a valid char`() {
        assertThrows(ConversionException::class.java) {
            cut.convertValueOnly("")
        }
    }

    @Test
    fun `one character string is a valid char`() {
        assertThat(cut.convertValueOnly("a")).isEqualTo('a')
    }

    @Test
    fun `two character string is not a valid char`() {
        assertThrows(ConversionException::class.java) {
            cut.convertValueOnly("ab")
        }
    }

    @Test
    fun `converter can converted to Kotlin Char`() {
        assertThat(cut.canConvertTo(Char::class)).isTrue()
    }
}
