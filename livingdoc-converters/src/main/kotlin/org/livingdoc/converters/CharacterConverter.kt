package org.livingdoc.converters

import java.lang.reflect.AnnotatedElement
import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.TypeConverter

/**
 * This converter is used to convert a String with a length of 1 to a char
 */
open class CharacterConverter : TypeConverter<Char> {

    /**
     * This function converts the given parameter value with a length of 1 to a char. If the length of value is not 1,
     * this function throws a Conversion exception.
     *
     * @param value the String that should have a length of 1
     * @return the char value of the given String
     * @throws ConversionException if the length is not 1
     */
    override fun convert(value: String, element: AnnotatedElement?, documentClass: Class<*>?): Char {
        if (value.length != 1) {
            throw ConversionException("not a char value: '$value'")
        }
        return value[0]
    }

    override fun canConvertTo(targetType: Class<*>): Boolean {
        val isJavaObjectType = Char::class.javaObjectType == targetType
        val isKotlinType = Char::class.java == targetType
        return isJavaObjectType || isKotlinType
    }
}
