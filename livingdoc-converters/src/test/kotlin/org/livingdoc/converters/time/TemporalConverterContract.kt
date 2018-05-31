package org.livingdoc.converters.time

import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.livingdoc.api.conversion.Format
import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.exceptions.MalformedFormatException
import org.livingdoc.converters.exceptions.ValueFormatException
import org.mockito.BDDMockito.given
import java.lang.reflect.AnnotatedElement
import java.time.temporal.Temporal


internal abstract class TemporalConverterContract<T : Temporal> {

    abstract val cut: TypeConverter<T>

    abstract val validInputVariations: Map<String, T>

    abstract val defaultFormatValue: Pair<String, T>

    abstract val customFormat: String
    abstract val customFormatValue: Pair<String, T>
    abstract val malformedCustomFormat: String

    @TestFactory
    fun `default input format variations`(): List<DynamicTest> {
        return validInputVariations
            .map { (value, expectedResult) ->
                dynamicTest("$value is valid input format", {
                    val result = cut.convert(value, null, null)
                    assertThat(result).isEqualTo(expectedResult)
                })
            }
    }


    @Test fun `non temporal cannot be converted`() {
        assertThrows(ValueFormatException::class.java) {
            cut.convert("not a temporal value", null, null)
        }
    }

    @Nested inner class `custom input format` {

        private val format: Format = mock()
        private val element: AnnotatedElement = mock()

        @Test fun `default format used if no element given`() {
            val (value, expectedResult) = defaultFormatValue
            val date = cut.convert(value, null, null)
            assertThat(date).isEqualTo(expectedResult)
        }

        @Test fun `default format used if no annotation present`() {
            given(element.getAnnotation(Format::class.java)).willReturn(null)

            val (value, expectedResult) = defaultFormatValue
            val date = cut.convert(value, element, null)
            assertThat(date).isEqualTo(expectedResult)
        }

        @Test fun `format can be overridden via annotation`() {
            given(element.getAnnotation(Format::class.java)).willReturn(format)
            given(format.value).willReturn(customFormat)

            val (value, expectedResult) = customFormatValue
            val date = cut.convert(value, element, null)
            assertThat(date).isEqualTo(expectedResult)
        }

        @Test fun `malformed custom pattern throws exception`() {
            given(element.getAnnotation(Format::class.java)).willReturn(format)
            given(format.value).willReturn(malformedCustomFormat)

            val (value) = customFormatValue
            assertThrows(MalformedFormatException::class.java) {
                cut.convert(value, element, null)
            }
        }
    }
}
