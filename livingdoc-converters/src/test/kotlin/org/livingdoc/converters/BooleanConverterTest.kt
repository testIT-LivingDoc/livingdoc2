package org.livingdoc.converters

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.livingdoc.api.conversion.ConversionException


internal class BooleanConverterTest : DefaultTypeConverterContract {

    override val cut = BooleanConverter()

    @ParameterizedTest(name = "\"{0}\" is converted to: {1}")
    @CsvSource("true, true",
            "TRUE, true",
            "TrUe, true",
            "false, false",
            "FALSE, false",
            "FaLsE, false")
    fun `values are converted correctly`(value: String, expected: Boolean) {
        assertThat(cut.convert(value, null, null)).isEqualTo(expected)
    }

    @Test fun `leading whitespaces are removed`() {
        assertThat(cut.convert(" true", null, null)).isTrue()
        assertThat(cut.convert("\ttrue", null, null)).isTrue()
        assertThat(cut.convert("\ntrue", null, null)).isTrue()
    }

    @Test fun `trailing whitespaces are removed`() {
        assertThat(cut.convert("true ", null, null)).isTrue()
        assertThat(cut.convert("true\t", null, null)).isTrue()
        assertThat(cut.convert("true\n", null, null)).isTrue()
    }

    @Test fun `illegal value throws ConversionException`() {
        assertThrows(ConversionException::class.java) { cut.convert("neither", null, null) }
    }

    @Test fun `converter can converted to Kotlin Boolean`() {
        assertThat(cut.canConvertTo(Boolean::class.java)).isTrue()
    }
}
