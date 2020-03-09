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
    @CsvSource(
        "true, true",
        "TRUE, true",
        "TrUe, true",
        "false, false",
        "FALSE, false",
        "FaLsE, false"
    )
    fun `values are converted correctly`(value: String, expected: Boolean) {
        assertThat(cut.convertValueOnly(value)).isEqualTo(expected)
    }

    @Test
    fun `leading whitespaces are removed`() {
        assertThat(cut.convertValueOnly(" true")).isTrue()
        assertThat(cut.convertValueOnly("\ttrue")).isTrue()
        assertThat(cut.convertValueOnly("\ntrue")).isTrue()
    }

    @Test
    fun `trailing whitespaces are removed`() {
        assertThat(cut.convertValueOnly("true ")).isTrue()
        assertThat(cut.convertValueOnly("true\t")).isTrue()
        assertThat(cut.convertValueOnly("true\n")).isTrue()
    }

    @Test
    fun `illegal value throws ConversionException`() {
        assertThrows(ConversionException::class.java) { cut.convertValueOnly("neither") }
    }

    @Test
    fun `converter can converted to Kotlin Boolean`() {
        assertThat(cut.canConvertTo(Boolean::class)).isTrue()
        assertThat(cut.canConvertTo(java.lang.Boolean::class)).isTrue()
        assertThat(cut.canConvertTo(Boolean::class.javaObjectType.kotlin)).isTrue()
        assertThat(cut.canConvertTo(Boolean::class.javaPrimitiveType!!.kotlin)).isTrue()
    }
}
