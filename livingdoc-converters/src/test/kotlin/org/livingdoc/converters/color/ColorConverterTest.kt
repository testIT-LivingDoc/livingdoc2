package org.livingdoc.converters.color

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.livingdoc.converters.exceptions.ColorFormatException

internal class ColorConverterTest {

    private val colorConverter : ColorConverter = ColorConverter()

    private val emptyColorString = ""

    private val correctHexValue1 = "#66ff33"
    private val correctHexValue2 = "#00bfff"
    private val inCorrectHexValue = "#2P9k02"

    private val correctColorName1 = "green"
    private val correctColorName2 = "blue"
    private val incorrectColorName = "darg"
    private val notPresentColorName = "pink"

    private val correctRgbValue1 = "rgb( 122, 234, 100)"
    private val correctRgbValue2 = "rgb(0,191,255)"
    private val incorrectRgbValue = "rgb(180,180,456) "

    private val nullColorValue = null

    @Test
    fun testEmptyOrNullColorString(){
        assertThrows(ColorFormatException::class.java){
            colorConverter.convert(emptyColorString, null, null)
        }

        assertThrows(ColorFormatException::class.java){
            colorConverter.convert(nullColorValue, null, null)
        }
    }

    @Test
    fun testHexValues(){

        assertThat(colorConverter.convert(correctHexValue1, null, null)).isEqualTo("#66ff33")
        assertThat(colorConverter.convert(correctHexValue2, null, null)).isEqualTo("#00bfff")

        assertThrows(ColorFormatException::class.java){
            colorConverter.convert(inCorrectHexValue, null, null)
        }
    }

    @Test
    fun testColorNames(){
        assertThat(colorConverter.convert(correctColorName1, null, null).toLowerCase()).isEqualTo("#008000")
        assertThat(colorConverter.convert(correctColorName2, null, null).toLowerCase()).isEqualTo("#0000ff")

        assertThrows(ColorFormatException::class.java){
            colorConverter.convert(incorrectColorName, null, null)
        }

        assertThrows(ColorFormatException::class.java){
            colorConverter.convert(notPresentColorName, null, null)
        }

    }

    @Test
    fun testRgbValues(){
        assertThat(colorConverter.convert(correctRgbValue1, null, null)).isEqualTo("#7aea64")
        assertThat(colorConverter.convert(correctRgbValue2, null, null)).isEqualTo("#00bfff")

        assertThrows(ColorFormatException::class.java){
            colorConverter.convert(incorrectRgbValue, null, null)
        }

    }
}