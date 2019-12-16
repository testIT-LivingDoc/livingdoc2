package org.livingdoc.converters.color

import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.exceptions.ColorFormatException
import java.lang.reflect.AnnotatedElement
import java.util.*

/**
 * A class containing methods, that can convert a string to a hex color value.
 */
open class ColorConverter : TypeConverter<String> {

    private val hexRegexString = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})\$"

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

        val hexPattern = hexRegexString.toPattern()
        val hexMatcher = hexPattern.matcher(lowerCaseAndTrimmedValue)

        if (hexMatcher.matches()) {

            if (lowerCaseAndTrimmedValue.removePrefix("#").length == 3) {
                var removedPrefixedVal = lowerCaseAndTrimmedValue.removePrefix("#")
                var result = "#"

                for (letter: Char in removedPrefixedVal) {
                    result += letter.toString() + letter.toString()
                }

                return result
            }

            return lowerCaseAndTrimmedValue
        } else if (lowerCaseAndTrimmedValue.startsWith("rgb(") && value.endsWith(")")) {
            val splittedColorValues: List<String> =
                lowerCaseAndTrimmedValue.substring(4, lowerCaseAndTrimmedValue.length - 1).split(",")
            var colorHexValue = "#"

            if (splittedColorValues.size != 3) {
                throw ColorFormatException(lowerCaseAndTrimmedValue)
            } else {

                for (colorValue in splittedColorValues) {
                    val colorValueInt =
                        try {
                            colorValue.toInt()
                        } catch (nfe: NumberFormatException) {
                            throw ColorFormatException(lowerCaseAndTrimmedValue)
                        }

                    if (colorValueInt in 0..255) {
                        var hexString = Integer.toHexString(colorValueInt)
                        if (hexString.length == 1) {
                            hexString = "0$hexString"
                        }
                        colorHexValue += hexString
                    } else {
                        throw ColorFormatException(lowerCaseAndTrimmedValue)
                    }
                }
                return colorHexValue.toLowerCase()
            }
        } else {
            return prop.getProperty(lowerCaseAndTrimmedValue)?.toLowerCase() ?: throw ColorFormatException(
                lowerCaseAndTrimmedValue
            )
        }
    }
}
