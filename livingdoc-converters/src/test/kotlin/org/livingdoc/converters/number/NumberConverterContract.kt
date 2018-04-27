package org.livingdoc.converters.number

import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.Language
import org.mockito.BDDMockito.given
import utils.EnglishDefaultLocale
import java.lang.reflect.AnnotatedElement

@EnglishDefaultLocale
internal abstract class NumberConverterContract<T : Number> {

    abstract val cut: AbstractNumberConverter<T>

    abstract val minValue: T
    abstract val negativeValue: T
    abstract val zeroValue: T
    abstract val positiveValue: T
    abstract val maxValue: T

    abstract val englishValue: Pair<String, T>
    abstract val germanValue: Pair<String, T>

    @Test fun `the smallest possible value can be converted`() {
        assertThatValueCanBeConverted(minValue)
    }


    @Test fun `negative value can be converted`() {
        assertThatValueCanBeConverted(negativeValue)
    }


    @Test fun `zero value can be converted`() {
        assertThatValueCanBeConverted(zeroValue)
    }

    @Test fun `positive value can be converted`() {
        assertThatValueCanBeConverted(positiveValue)
    }

    @Test fun `the largest possible value can be converted`() {
        assertThatValueCanBeConverted(maxValue)
    }

    private fun assertThatValueCanBeConverted(value: T) {
        val result = cut.convert("$value", null, null)
        assertThat(result).isEqualTo(value)
    }

    @Test fun `leading whitespaces are ignored`() {
        assertThatValueCanBeConverted(" $positiveValue", positiveValue)
        assertThatValueCanBeConverted("\t$positiveValue", positiveValue)
        assertThatValueCanBeConverted("\n$positiveValue", positiveValue)
    }

    @Test fun `trailing whitespaces are ignored`() {
        assertThatValueCanBeConverted("$positiveValue ", positiveValue)
        assertThatValueCanBeConverted("$positiveValue\t", positiveValue)
        assertThatValueCanBeConverted("$positiveValue\n", positiveValue)
    }

    private fun assertThatValueCanBeConverted(value: String, expected: T) {
        val result = cut.convert(value, null, null)
        assertThat(result).isEqualTo(expected)
    }

    @Test fun `non number cannot be converted`() {
        assertThrows(ConversionException::class.java) {
            cut.convert("not a number", null, null)
        }
    }

    @Nested inner class localization {

        val language: Language = mock()
        val element: AnnotatedElement = mock()

        @Test fun `default locale used if no element given`() {
            val (stringValue, value) = englishValue
            val result = cut.convert(stringValue, null, null)
            assertThat(result).isEqualTo(value)
        }

        @Test fun `default locale used if no annotation present`() {
            given(element.getAnnotation(Language::class.java)).willReturn(null)

            val (stringValue, value) = englishValue
            val result = cut.convert(stringValue, element, null)
            assertThat(result).isEqualTo(value)
        }

        @Test fun `locale can be overridden via annotation`() {
            given(element.getAnnotation(Language::class.java)).willReturn(language)
            given(language.value).willReturn("de")

            val (stringValue, value) = germanValue
            val result = cut.convert(stringValue, element, null)
            assertThat(result).isEqualTo(value)
        }


    }


}
