package org.livingdoc.converters.color

import org.livingdoc.api.conversion.Context
import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.exceptions.ColorFormatException
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * A class containing methods, that can convert a string to a hex color value.
 */
class ColorConverter : TypeConverter<String> {

    private val prop: Properties = Properties()

    init {
        val fis = ColorConverter::class.java.getResourceAsStream("/properties/color.properties")
        prop.load(fis)
    }

    override fun canConvertTo(targetType: KClass<*>): Boolean = String::class == targetType

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
    override fun convert(value: String, type: KType, context: Context): String {
        val lowerCaseAndTrimmedValue = value.toLowerCase().trim().replace(" ", "")

        return parseHexColor(lowerCaseAndTrimmedValue)
            ?: parseRgbColor(lowerCaseAndTrimmedValue)
            ?: lookupColorByName(lowerCaseAndTrimmedValue)
            ?: throw ColorFormatException(lowerCaseAndTrimmedValue)
    }

    private fun parseHexColor(hexColor: String): String? {
        if (isLongHexColor(hexColor)) {
            return hexColor
        }

        return parseShortHexColor(hexColor)
    }

    private fun isLongHexColor(color: String): Boolean {
        val regex = "#[a-f0-9]{6}".toRegex()
        return color.matches(regex)
    }

    private fun parseShortHexColor(hexColor: String): String? {
        if (!isShortHexColor(hexColor)) {
            return null
        }

        val removedPrefixedVal = hexColor.removePrefix("#")
        var result = "#"

        for (letter: Char in removedPrefixedVal) {
            result += letter.toString() + letter.toString()
        }

        return result
    }

    private fun isShortHexColor(color: String): Boolean {
        val regex = "#[a-f0-9]{3}".toRegex()
        return color.matches(regex)
    }

    private fun isRgbColor(color: String): Boolean {
        return color.startsWith("rgb(") && color.endsWith(")")
    }

    private fun parseRgbColor(rgbColor: String): String? {
        if (!isRgbColor(rgbColor)) {
            return null
        }

        val rgbValues: List<String> =
            rgbColor.removeSurrounding("rgb(", ")").split(",")

        if (rgbValues.size != rgbComponentCount) {
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

            if (colorValueInt !in rgbComponentRange) {
                throw ColorFormatException(rgbColor)
            }

            var hexString = Integer.toHexString(colorValueInt)
            if (hexString.length == 1) {
                hexString = "0$hexString"
            }
            colorHexValue += hexString
        }

        return colorHexValue.toLowerCase()
    }

    private fun lookupColorByName(colorName: String): String? {
        return prop.getProperty(colorName)?.toLowerCase()
    }

    companion object {
        private const val rgbComponentCount = 3
        private val rgbComponentRange = 0..255
    }
}
