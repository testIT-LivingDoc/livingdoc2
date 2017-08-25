package org.livingdoc.converters

import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.TypeConverter
import java.lang.reflect.AnnotatedElement


open class BooleanConverter : TypeConverter<Boolean> {

    @Throws(ConversionException::class)
    override fun convert(value: String, element: AnnotatedElement?, documentClass: Class<*>?): Boolean? {
        val trimmedLowerCaseValue = value.trim().toLowerCase()
        when (trimmedLowerCaseValue) {
            "true" -> return true
            "false" -> return false
        }
        throw ConversionException("Not a boolean value: '$value'")
    }

    override fun canConvertTo(targetType: Class<*>): Boolean {
        val isJavaObjectType = Boolean::class.javaObjectType == targetType
        val isKotlinType = Boolean::class.java == targetType
        return isJavaObjectType || isKotlinType
    }

}
