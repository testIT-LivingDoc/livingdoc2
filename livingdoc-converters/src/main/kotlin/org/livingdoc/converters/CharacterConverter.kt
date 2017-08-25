package org.livingdoc.converters

import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.TypeConverter
import java.lang.reflect.AnnotatedElement


open class CharacterConverter : TypeConverter<Char> {

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
