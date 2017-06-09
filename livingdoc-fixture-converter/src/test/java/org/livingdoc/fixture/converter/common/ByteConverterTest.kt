package org.livingdoc.fixture.converter.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.livingdoc.fixture.api.converter.ConversionException
import utils.EnglishDefaultLocale

@EnglishDefaultLocale
internal class ByteConverterTest {

    val cut = ByteConverter()

    @Test
    fun `smallest byte can be converted`() {
        val value = Byte.MIN_VALUE.toString()
        val result = cut.convert(value)
        assertThat(result).isEqualTo(Byte.MIN_VALUE)
    }

    @Test
    fun `largest byte can be converted`() {
        val value = Byte.MAX_VALUE.toString()
        val result = cut.convert(value)
        assertThat(result).isEqualTo(Byte.MAX_VALUE)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = arrayOf(
            "-100", "-42", "-10", "-1",
            "0",
            "1", "10", "42", "100"
    ))
    fun `any byte can be converted`(value: Byte) {
        val valueAsString = value.toString()
        assertThat(cut.convert(valueAsString)).isEqualTo(value)
    }

    @Test
    fun `leading whitespaces are removed`() {
        assertThat(cut.convert(" 1")).isEqualTo(1)
        assertThat(cut.convert("\t1")).isEqualTo(1)
        assertThat(cut.convert("\n1")).isEqualTo(1)
    }

    @Test
    fun `trailing whitespaces are removed`() {
        assertThat(cut.convert("1 ")).isEqualTo(1)
        assertThat(cut.convert("1\t")).isEqualTo(1)
        assertThat(cut.convert("1\n")).isEqualTo(1)
    }

    @Test
    fun `illegal format throws ConversionException`() {
        assertThrows(ConversionException::class.java) {
            cut.convert("hello world")
        }
    }

}
