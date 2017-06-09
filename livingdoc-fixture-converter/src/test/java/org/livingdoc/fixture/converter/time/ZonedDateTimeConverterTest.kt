package org.livingdoc.fixture.converter.time

import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions
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
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


internal class ZonedDateTimeConverterTest {

    val cut = ZonedDateTimeConverter()

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = arrayOf(
            "2017-05-12T12:34+02:00[Europe/Berlin]",
            "2017-05-12T12:34:56+02:00[Europe/Paris]",
            "2017-05-12T12:34:56.123+01:00[Europe/London]"
    ))
    fun `zoned date times are converted correctly`(value: String) {
        val parsed = ZonedDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME)
        Assertions.assertThat(cut.convert(value)).isEqualTo(parsed)
    }

    @Nested
    inner class FormatOverride {

        val format: Format = mock()
        val element: AnnotatedElement = mock()

        @Test
        fun `default format assumed if no element given`() {
            val time = cut.convert("2017-05-12T12:34+01:00[Europe/Berlin]", null)
            Assertions.assertThat(time).isEqualTo("2017-05-12T12:34+01:00[Europe/Berlin]")
        }

        @Test
        fun `default format assumed if no annotation present`() {
            given(element.getAnnotation(Format::class.java)).willReturn(null)

            val time = cut.convert("2017-05-12T12:34+01:00[Europe/Berlin]", element)
            Assertions.assertThat(time).isEqualTo("2017-05-12T12:34+01:00[Europe/Berlin]")
        }

        @Test
        fun `pattern can be overridden via annotation`() {
            given(element.getAnnotation(Format::class.java)).willReturn(format)
            given(format.value).willReturn("dd.MM.uuuu HH:mm 'Uhr' X VV")

            val time = cut.convert("12.05.2017 12:34 Uhr +01 Europe/Berlin", element)
            Assertions.assertThat(time).isEqualTo("2017-05-12T12:34+01:00[Europe/Berlin]")
        }

        @Test
        fun `malformed custom pattern throws exception`() {
            given(element.getAnnotation(Format::class.java)).willReturn(format)
            given(format.value).willReturn("dd.MM.uuuu HH:mm X V")

            assertThrows(MalformedFormatException::class.java) {
                cut.convert("12.05.2017 12:34 +01 Europe/Berlin", element)
            }
        }

    }

    @Test
    fun `illegal value throws exception`() {
        assertThrows(ValueFormatException::class.java) {
            cut.convert("not Date/Time")
        }
    }

}
