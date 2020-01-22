package org.livingdoc.converters

import java.lang.reflect.AnnotatedElement
import org.livingdoc.api.conversion.TypeConverter

/**
 * This converter is used to convert a String to a String
 */
open class StringConverter : TypeConverter<String> {

    /**
     * This method 'converts' the given value to a String by returning the input string.
     * @param value: the input String
     * @return the result of the conversion as a String
     */
    override fun convert(value: String, element: AnnotatedElement?, documentClass: Class<*>?): String = value

    /**
     * This method returns true, if the parameter targetType is the java String class.
     */
    override fun canConvertTo(targetType: Class<*>) = String::class.java == targetType
}
