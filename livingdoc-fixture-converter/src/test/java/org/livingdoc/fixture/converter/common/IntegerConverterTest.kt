package org.livingdoc.fixture.converter.common

import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.livingdoc.fixture.api.converter.ConversionException
import org.livingdoc.fixture.api.converter.Language
import org.mockito.BDDMockito.given
import utils.EnglishDefaultLocale
import java.lang.Integer.MAX_VALUE
import java.lang.Integer.MIN_VALUE
import java.lang.reflect.AnnotatedElement
import java.util.*

@EnglishDefaultLocale
internal class IntegerConverterTest {

    val cut = IntegerConverter()

    @Test
    fun `zero value can be converted`() {
        val result = cut.convert("0")
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `smallest integer can be converted`() {
        val value = MIN_VALUE.toString()
        val result = cut.convert(value)
        assertThat(result).isEqualTo(MIN_VALUE)
    }

    @Test
    fun `small integer can be converted`() {
        val value = "-10000000"
        val result = cut.convert(value)
        assertThat(result).isEqualTo(-10000000)
    }

    @Test
    fun `largest integer can be converted`() {
        val value = MAX_VALUE.toString()
        val result = cut.convert(value)
        assertThat(result).isEqualTo(MAX_VALUE)
    }

    @Test
    fun `large integer can be converted`() {
        val value = "10000000"
        val result = cut.convert(value)
        assertThat(result).isEqualTo(10000000)
    }

    @Test
    fun `any integer can be converted`() {
        Random().ints(1000)//
                .forEach { value ->
                    val positiveValue = value.toString()
                    val negativeValue = (-value).toString()
                    assertThat(cut.convert(positiveValue)).isEqualTo(value)
                    assertThat(cut.convert(negativeValue)).isEqualTo(-value)
                }
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

    @Nested
    inner class LanguageOverride {

        val language: Language = mock()
        val element: AnnotatedElement = mock()

        @Test
        fun `default language assumed if no element given`() {
            val result = cut.convert("42,000", null)
            assertThat(result).isEqualTo(42000)
        }

        @Test
        fun `default language assumed if no annotation present`() {
            given(element.getAnnotation(Language::class.java)).willReturn(null)

            val result = cut.convert("42,000", element)
            assertThat(result).isEqualTo(42000)
        }

        @Test
        fun `language can be overridden via annotation`() {
            given(element.getAnnotation(Language::class.java)).willReturn(language)
            given(language.value).willReturn("de")

            val result = cut.convert("42.000", element)
            assertThat(result).isEqualTo(42000)
        }

    }

}
