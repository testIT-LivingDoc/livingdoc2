package org.livingdoc.converters.color

import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.exceptions.ColorFormatException
import java.lang.reflect.AnnotatedElement
import java.util.*

/**
 * A class containing methods, that can convert a string to a hex color value.
 */
open class ColorConverter : TypeConverter<String> {

    private val prop: Properties = Properties()

    init {
        val fis = ColorConverter::class.java.getResourceAsStream("/properties/color.properties")
        prop.load(fis)
    }

    override fun canConvertTo(targetType: Class<*>?): Boolean = String::class.java == targetType

    /**
     * Converts a given string to a lower case hex color value string.
     * This method throws a @throws ColorFormatException if the given string is not valid color value.
     * In the context of this function valid color values, that can be converted are  hex color values
     * e.g. #66ff33, pre-defined color names in a property file e.g. blue, or rgb color values
     * e.g. rgb(0,191,255).
     *
     * @param value - the string to be converted
     * @param element
     * @param documentClass
     *
     * @return A lower case hex color value as a string.
     */
    override fun convert(value: String?, element: AnnotatedElement?, documentClass: Class<*>?): String {
        if (value.isNullOrEmpty()) {
            throw ColorFormatException(value)
        }

        val lowerCaseAndTrimmedValue = value.toLowerCase().trim().replace(" ", "")

        return when {
            isHexColor(lowerCaseAndTrimmedValue) -> {
                parseHexColor(lowerCaseAndTrimmedValue)
            }
            isRgbColor(lowerCaseAndTrimmedValue) -> {
                parseRgbColor(lowerCaseAndTrimmedValue)
            }
            else -> {
                lookupColorByName(lowerCaseAndTrimmedValue)
            }
        }
    }

    private fun isHexColor(color: String): Boolean {
        return color.matches(Companion.hexColorRegex)
    }

    private fun parseHexColor(hexColor: String): String {
        if (hexColor.removePrefix("#").length == 3) {
            return hexColor
        }

        var removedPrefixedVal = hexColor.removePrefix("#")
        var result = "#"

        for (letter: Char in removedPrefixedVal) {
            result += letter.toString() + letter.toString()
        }

        return result
    }

    private fun isRgbColor(color: String) =
        color.startsWith("rgb(") && color.endsWith(")")

    private fun parseRgbColor(rgbColor: String): String {
        val rgbValues: List<String> =
            rgbColor.removeSurrounding("rgb(", ")").split(",")

        if (rgbValues.size != 3) {
            throw ColorFormatException(rgbColor)
        }

        var colorHexValue = "#"
        for (colorValue in rgbValues) {
            val colorValueInt =
                try {
                    colorValue.toInt()
                } catch (nfe: NumberFormatException) {
                    throw ColorFormatException(rgbColor)
                }

            if (colorValueInt in 0..255) {
                var hexString = Integer.toHexString(colorValueInt)
                if (hexString.length == 1) {
                    hexString = "0$hexString"
                }
                colorHexValue += hexString
            } else {
                throw ColorFormatException(rgbColor)
            }
        }
        return colorHexValue.toLowerCase()
    }

    private fun lookupColorByName(colorName: String): String {
        return prop.getProperty(colorName)?.toLowerCase() ?: throw ColorFormatException(colorName)
    }

    companion object {
        private val hexColorRegex = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})\$".toRegex()
    }
}
