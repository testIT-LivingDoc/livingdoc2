package org.livingdoc.converters.color

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.livingdoc.converters.exceptions.ColorFormatException

internal class ColorConverterTest {

    private val colorConverter: ColorConverter = ColorConverter()

    @ParameterizedTest
    @CsvFileSource(resources = ["/colorData.csv"], numLinesToSkip = 1, delimiter = '=')
    fun testValidColorValues(input: String, expected: String) {
        assertEquals(colorConverter.convert(input, null, null), expected.toLowerCase())
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/invalidColorData.csv"], numLinesToSkip = 1, delimiter = '=')
    fun testInvalidColorValues(input: String) {
        assertThrows(ColorFormatException::class.java) {
            colorConverter.convert(input, null, null)
        }
    }

    @Test
    fun testEmptyOrNullColorString() {
        assertThrows(ColorFormatException::class.java) {
            colorConverter.convert("", null, null)
        }
    }
}
