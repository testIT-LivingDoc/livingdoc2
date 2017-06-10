package org.livingdoc.fixture.converter.number

import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.livingdoc.fixture.api.converter.ConversionException
import org.livingdoc.fixture.api.converter.Language
import org.livingdoc.fixture.api.converter.TypeConverter
import org.mockito.BDDMockito.given
import utils.EnglishDefaultLocale
import java.lang.reflect.AnnotatedElement

@EnglishDefaultLocale
internal abstract class NumberConverterContract<T : Number> {

    abstract fun getCut(): TypeConverter<T>

    abstract fun getMinValue(): T
    abstract fun getNegativeValue(): T
    abstract fun getZeroValue(): T
    abstract fun getPositiveValue(): T
    abstract fun getMaxValue(): T

    abstract fun getEnglishValue(): Pair<String, T>
    abstract fun getGermanValue(): Pair<String, T>

    @Test
    fun `the smallest possible value can be converted`() {
        assertThatValueCanBeConverted(getMinValue())
    }

    @Test
    fun `negative value can be converted`() {
        assertThatValueCanBeConverted(getNegativeValue())
    }

    @Test
    fun `zero value can be converted`() {
        assertThatValueCanBeConverted(getZeroValue())
    }

    @Test
    fun `positive value can be converted`() {
        assertThatValueCanBeConverted(getPositiveValue())
    }

    @Test
    fun `the largest possible value can be converted`() {
        assertThatValueCanBeConverted(getMaxValue())
    }

    private fun assertThatValueCanBeConverted(value: T) {
        val result = getCut().convert("$value")
        assertThat(result).isEqualTo(value)
    }

    @Test
    fun `leading whitespaces are ignored`() {
        assertThatValueCanBeConverted(" ${getPositiveValue()}", getPositiveValue())
        assertThatValueCanBeConverted("\t${getPositiveValue()}", getPositiveValue())
        assertThatValueCanBeConverted("\n${getPositiveValue()}", getPositiveValue())
    }

    @Test
    fun `trailing whitespaces are ignored`() {
        assertThatValueCanBeConverted("${getPositiveValue()} ", getPositiveValue())
        assertThatValueCanBeConverted("${getPositiveValue()}\t", getPositiveValue())
        assertThatValueCanBeConverted("${getPositiveValue()}\n", getPositiveValue())
    }

    private fun assertThatValueCanBeConverted(value: String, expected: T) {
        val result = getCut().convert(value)
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `non number cannot be converted`() {
        assertThrows(ConversionException::class.java) {
            getCut().convert("not a number")
        }
    }

    @Nested
    inner class localization {

        val language: Language = mock()
        val element: AnnotatedElement = mock()

        @Test
        fun `default locale used if no element given`() {
            val (stringValue, value) = getEnglishValue()
            val result = getCut().convert(stringValue, null)
            assertThat(result).isEqualTo(value)
        }

        @Test
        fun `default locale used if no annotation present`() {
            given(element.getAnnotation(Language::class.java)).willReturn(null)

            val (stringValue, value) = getEnglishValue()
            val result = getCut().convert(stringValue, element)
            assertThat(result).isEqualTo(value)
        }

        @Test
        fun `locale can be overridden via annotation`() {
            given(element.getAnnotation(Language::class.java)).willReturn(language)
            given(language.value).willReturn("de")

            val (stringValue, value) = getGermanValue()
            val result = getCut().convert(stringValue, element)
            assertThat(result).isEqualTo(value)
        }


    }


}
