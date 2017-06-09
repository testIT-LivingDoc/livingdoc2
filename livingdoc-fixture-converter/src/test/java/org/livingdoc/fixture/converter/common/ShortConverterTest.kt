package org.livingdoc.fixture.converter.common

import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.livingdoc.fixture.api.converter.ConversionException
import org.livingdoc.fixture.api.converter.Language
import org.mockito.BDDMockito.given
import utils.EnglishDefaultLocale
import java.lang.reflect.AnnotatedElement

@EnglishDefaultLocale
internal class ShortConverterTest {

    val cut = ShortConverter()

    @Test
    fun `smallest short can be converted`() {
        val value = Short.MIN_VALUE.toString()
        val result = cut.convert(value)
        assertThat(result).isEqualTo(Short.MIN_VALUE)
    }

    @Test
    fun `largest short can be converted`() {
        val value = Short.MAX_VALUE.toString()
        val result = cut.convert(value)
        assertThat(result).isEqualTo(Short.MAX_VALUE)
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = arrayOf(
            "-100", "-42", "-10", "-1",
            "0",
            "1", "10", "42", "100"
    ))
    fun `any short can be converted`(value: Short) {
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
        Assertions.assertThrows(ConversionException::class.java) {
            cut.convert("hello world")
        }
    }

    @Nested
    inner class LanguageOverride {

        val language: Language = mock()
        val element: AnnotatedElement = mock()

        @Test
        fun `default language assumed if no element given`() {
            val result = cut.convert("10,000", null)
            assertThat(result).isEqualTo(10000)
        }

        @Test
        fun `default language assumed if no annotation present`() {
            given(element.getAnnotation(Language::class.java)).willReturn(null)

            val result = cut.convert("10,000", element)
            assertThat(result).isEqualTo(10000)
        }

        @Test
        fun `language can be overridden via annotation`() {
            given(element.getAnnotation(Language::class.java)).willReturn(language)
            given(language.value).willReturn("de")

            val result = cut.convert("10.000", element)
            assertThat(result).isEqualTo(10000)
        }

    }

}
