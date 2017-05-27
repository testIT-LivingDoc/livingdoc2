package org.livingdoc.fixture.converter.time

import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.livingdoc.fixture.api.converter.Format
import org.livingdoc.fixture.converter.exceptions.MalformedFormatException
import org.livingdoc.fixture.converter.exceptions.ValueFormatException
import org.mockito.BDDMockito.given
import java.lang.reflect.AnnotatedElement
import java.time.LocalDate
import java.time.format.DateTimeFormatter


internal class LocalDateConverterTest {

    val cut = LocalDateConverter()

    @Test
    fun `ISO dates are converted correctly`() {
        val parsed = LocalDate.parse("2017-05-12", DateTimeFormatter.ISO_LOCAL_DATE)
        assertThat(cut.convert("2017-05-12")).isEqualTo(parsed)
    }

    @Nested
    inner class FormatOverride {

        val format: Format = mock()
        val element: AnnotatedElement = mock()

        @Test
        fun `default format assumed if no element given`() {
            val date = cut.convert("2017-05-12", null)
            assertThat(date).isEqualTo("2017-05-12")
        }

        @Test
        fun `default format assumed if no annotation present`() {
            given(element.getAnnotation(Format::class.java)).willReturn(null)

            val date = cut.convert("2017-05-12", element)
            assertThat(date).isEqualTo("2017-05-12")
        }

        @Test
        fun `pattern can be overridden via annotation`() {
            given(element.getAnnotation(Format::class.java)).willReturn(format)
            given(format.value).willReturn("dd.MM.uuuu")

            val date = cut.convert("12.05.2017", element)
            assertThat(date).isEqualTo("2017-05-12")
        }

        @Test
        fun `malformed custom pattern throws exception`() {
            given(element.getAnnotation(Format::class.java)).willReturn(format)
            given(format.value).willReturn("dd.MM.uuuu V")

            assertThrows(MalformedFormatException::class.java) {
                cut.convert("2017-05-12", element)
            }
        }

    }

    @Test
    fun `illegal value throws conversion exception`() {
        assertThrows(ValueFormatException::class.java) {
            cut.convert("not Date")
        }
    }

}
