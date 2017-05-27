package org.livingdoc.fixture.converter.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.livingdoc.fixture.api.converter.ConversionException
import java.lang.Double.MAX_VALUE
import java.lang.Double.MIN_VALUE
import java.util.*


internal class DoubleConverterTest {

    val cut = DoubleConverter()

    @ParameterizedTest(name = "\"{0}\" is converted to 0.0d")
    @ValueSource(strings = arrayOf("0", "0.", "0.0", "0.00", "0.000", "0.0000"))
    fun `zero values can be converted`(value: String) {
        assertThat(cut.convert(value)).isEqualTo(0.0)
    }

    @Test
    fun `smallest double can be converted`() {
        val value = MIN_VALUE.toString()
        val result = cut.convert(value)
        assertThat(result).isEqualTo(MIN_VALUE)
    }

    @Test
    fun `small double can be converted`() {
        val value = "-10000000.0000001"
        val result = cut.convert(value)
        assertThat(result).isEqualTo(-10000000.0000001)
    }

    @Test
    fun `largest double can be converted`() {
        val value = MAX_VALUE.toString()
        val result = cut.convert(value)
        assertThat(result).isEqualTo(MAX_VALUE)
    }

    @Test
    fun `large double can be converted`() {
        val value = "10000000.0000001"
        val result = cut.convert(value)
        assertThat(result).isEqualTo(10000000.0000001)
    }

    @Test
    fun `any double can be converted`() {
        Random().doubles(1000)//
                .map { value -> value * (MAX_VALUE - 1.0) }//
                .forEach { value ->
                    val positiveValue = value.toString()
                    val negativeValue = (-value).toString()
                    assertThat(cut.convert(positiveValue)).isEqualTo(value)
                    assertThat(cut.convert(negativeValue)).isEqualTo(-value)
                }
    }

    @Test
    fun `leading whitespaces are removed`() {
        assertThat(cut.convert(" 1.0")).isEqualTo(1.0)
        assertThat(cut.convert("\t1.0")).isEqualTo(1.0)
        assertThat(cut.convert("\n1.0")).isEqualTo(1.0)
    }

    @Test
    fun `trailing whitespaces are removed`() {
        assertThat(cut.convert("1.0 ")).isEqualTo(1.0)
        assertThat(cut.convert("1.0\t")).isEqualTo(1.0)
        assertThat(cut.convert("1.0\n")).isEqualTo(1.0)
    }

    @Test
    fun `illegal format throws ConversionException`() {
        assertThrows(ConversionException::class.java) { cut.convert("hello world") }
    }

}
