package org.livingdoc.fixture.converter.time

import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.livingdoc.fixture.api.converter.Format
import org.livingdoc.fixture.converter.exceptions.MalformedFormatException
import org.livingdoc.fixture.converter.exceptions.ValueFormatException
import org.mockito.BDDMockito.given
import java.lang.reflect.AnnotatedElement
import java.time.LocalTime
import java.time.format.DateTimeFormatter


internal class LocalTimeConverterTest {

    val cut = LocalTimeConverter()

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = arrayOf(
            "12:34",
            "12:34:56",
            "12:34:56+01:00"
    ))
    fun `times are converted correctly`(value: String) {
        val parsed = LocalTime.parse(value, DateTimeFormatter.ISO_TIME)
        assertThat(cut.convert(value)).isEqualTo(parsed)
    }

    @Nested
    inner class FormatOverride {

        val format: Format = mock()
        val element: AnnotatedElement = mock()

        @Test
        fun `default format assumed if no element given`() {
            val time = cut.convert("12:34", null)
            assertThat(time).isEqualTo("12:34:00")
        }

        @Test
        fun `default format assumed if no annotation present`() {
            given(element.getAnnotation(Format::class.java)).willReturn(null)

            val time = cut.convert("12:34", element)
            assertThat(time).isEqualTo("12:34:00")
        }

        @Test
        fun `pattern can be overridden via annotation`() {
            given(element.getAnnotation(Format::class.java)).willReturn(format)
            given(format.value).willReturn("HH:mm 'Uhr'")

            val time = cut.convert("12:34 Uhr", element)
            assertThat(time).isEqualTo("12:34:00")
        }

        @Test
        fun `malformed custom pattern throws exception`() {
            given(element.getAnnotation(Format::class.java)).willReturn(format)
            given(format.value).willReturn("HH:mm V")

            assertThrows(MalformedFormatException::class.java) {
                cut.convert("12:34", element)
            }
        }

    }

    @Test
    fun `illegal value throws exception`() {
        assertThrows(ValueFormatException::class.java) {
            cut.convert("not Time")
        }
    }

}
