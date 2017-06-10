package org.livingdoc.fixture.converter.time

import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.livingdoc.fixture.api.converter.Format
import org.livingdoc.fixture.api.converter.TypeConverter
import org.livingdoc.fixture.converter.exceptions.MalformedFormatException
import org.livingdoc.fixture.converter.exceptions.ValueFormatException
import org.mockito.BDDMockito.given
import java.lang.reflect.AnnotatedElement
import java.time.temporal.Temporal


internal abstract class TemporalConverterContract<T : Temporal> {

    abstract fun getCut(): TypeConverter<T>

    abstract fun getValidInputVariations(): Map<String, T>

    abstract fun getDefaultFormatValue(): Pair<String, T>

    abstract fun getCustomFormat(): String
    abstract fun getCustomFormatValue(): Pair<String, T>
    abstract fun getMalformedCustomFormat(): String

    @TestFactory
    fun `default input format variations`(): List<DynamicTest> {
        return getValidInputVariations()
                .map { (value, expectedResult) ->
                    dynamicTest("$value is valid input format", {
                        val result = getCut().convert(value)
                        assertThat(result).isEqualTo(expectedResult)
                    })
                }
    }


    @Test
    fun `non temporal cannot be converted`() {
        assertThrows(ValueFormatException::class.java) {
            getCut().convert("not a temporal value")
        }
    }

    @Nested
    inner class `custom input format` {

        val format: Format = mock()
        val element: AnnotatedElement = mock()

        @Test
        fun `default format used if no element given`() {
            val (value, expectedResult) = getDefaultFormatValue()
            val date = getCut().convert(value, null)
            assertThat(date).isEqualTo(expectedResult)
        }

        @Test
        fun `default format used if no annotation present`() {
            given(element.getAnnotation(Format::class.java)).willReturn(null)

            val (value, expectedResult) = getDefaultFormatValue()
            val date = getCut().convert(value, element)
            assertThat(date).isEqualTo(expectedResult)
        }

        @Test
        fun `format can be overridden via annotation`() {
            given(element.getAnnotation(Format::class.java)).willReturn(format)
            given(format.value).willReturn(getCustomFormat())

            val (value, expectedResult) = getCustomFormatValue()
            val date = getCut().convert(value, element)
            assertThat(date).isEqualTo(expectedResult)
        }

        @Test
        fun `malformed custom pattern throws exception`() {
            given(element.getAnnotation(Format::class.java)).willReturn(format)
            given(format.value).willReturn(getMalformedCustomFormat())

            val (value) = getCustomFormatValue()
            assertThrows(MalformedFormatException::class.java) {
                getCut().convert(value, element)
            }
        }

    }

}
