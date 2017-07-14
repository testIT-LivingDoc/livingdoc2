package org.livingdoc.converters

import org.livingdoc.api.conversion.TypeConverter
import java.lang.reflect.AnnotatedElement


open class StringConverter : TypeConverter<String> {

    override fun convert(value: String, element: AnnotatedElement?) = value

    override fun canConvertTo(targetType: Class<*>): Boolean {
        val isJavaObjectType = String::class.javaObjectType == targetType
        val isKotlinType = String::class.java == targetType
        return isJavaObjectType || isKotlinType
    }

}
