package org.livingdoc.converters.color

import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.exceptions.ColorFormatException
import java.lang.reflect.AnnotatedElement
import java.util.*

open class ColorConverter : TypeConverter<String> {

    private val hexRegexString = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})\$"

    override fun canConvertTo(targetType: Class<*>?): Boolean = String::class.java == targetType

    override fun convert(value: String?, element: AnnotatedElement?, documentClass: Class<*>?): String {

        if (value.isNullOrEmpty()) {
            throw ColorFormatException(value)
        }

        val lowerCaseAndTrimmedValue = value.toLowerCase().trim().replace(" ", "")

        val hexPattern = hexRegexString.toPattern()
        val hexMatcher = hexPattern.matcher(lowerCaseAndTrimmedValue)
        val isHexValue = hexMatcher.matches()
        val isRgbValue = lowerCaseAndTrimmedValue.startsWith("rgb(") && value.endsWith(")")

        if (isHexValue) {
            return value
        } else if (isRgbValue) {
            val splittedColorValues: List<String> = lowerCaseAndTrimmedValue.substring(4, lowerCaseAndTrimmedValue.length - 1).split(",")
            var colorHexValue = "#"

            if (splittedColorValues.size != 3) {
                throw ColorFormatException(lowerCaseAndTrimmedValue)
            } else {

                for (colorValue in splittedColorValues) {
                    val colorValueInt = colorValue.toInt()

                    if (colorValueInt in 0..255) {
                        var hexString = Integer.toHexString(colorValueInt)
                        if (hexString.length == 1) {
                            hexString += hexString
                        }
                        colorHexValue += hexString
                    } else {
                        throw ColorFormatException(lowerCaseAndTrimmedValue)
                    }
                }
                return colorHexValue
            }
        } else {
            val fis = ColorConverter :: class.java.getResourceAsStream("/properties/color.properties")
            val prop = Properties()
            prop.load(fis)

            return prop.getProperty(lowerCaseAndTrimmedValue)?.toLowerCase() ?: throw ColorFormatException(lowerCaseAndTrimmedValue)
        }
    }
}
