package org.livingdoc.converters.color

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.livingdoc.api.conversion.Context
import org.livingdoc.converters.exceptions.ColorFormatException
import kotlin.reflect.KType

internal class ColorConverterTest {

    private val colorConverter: ColorConverter = ColorConverter()
    private val context = mockk<Context>()
    private val type = mockk<KType>()

    @ParameterizedTest
    @CsvFileSource(resources = ["/colorData.csv"], numLinesToSkip = 1, delimiter = '=')
    fun testValidColorValues(input: String, expected: String) {
        assertEquals(colorConverter.convert(input, type, context), expected.toLowerCase())
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/invalidColorData.csv"], numLinesToSkip = 1, delimiter = '=')
    fun testInvalidColorValues(input: String) {
        assertThrows(ColorFormatException::class.java) {
            colorConverter.convert(input, type, context)
        }
    }

    @Test
    fun testEmptyOrNullColorString() {
        assertThrows(ColorFormatException::class.java) {
            colorConverter.convert("", type, context)
        }
    }
}
