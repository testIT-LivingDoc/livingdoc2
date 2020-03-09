package org.livingdoc.converters

import org.livingdoc.api.conversion.Context
import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.TypeConverter
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * This converter is used to convert a String to a boolean, if the String is "true" or "false"
 */
open class BooleanConverter : TypeConverter<Boolean> {

    /**
     * This function converts the given parameter value with "true" or "false" to a boolean. If the content is not one
     * of these two values, the function throws a Conversion exception.
     *
     * @param value the String that should have "true" or "false" as a content
     * @return the boolean value of the given String
     * @throws ConversionException if the content is not "true" or "false"
     */
    @Throws(ConversionException::class)
    override fun convert(value: String, type: KType, context: Context): Boolean {
        val trimmedLowerCaseValue = value.trim().toLowerCase()
        when (trimmedLowerCaseValue) {
            "true" -> return true
            "false" -> return false
        }
        throw ConversionException("Not a boolean value: '$value'")
    }

    override fun canConvertTo(targetType: KClass<*>): Boolean {
        return Boolean::class == targetType
    }
}
