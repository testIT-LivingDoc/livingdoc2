package org.livingdoc.fixture.converter

import org.livingdoc.fixture.api.converter.TypeConverter
import java.lang.reflect.AnnotatedElement


open class StringConverter : TypeConverter<String> {

    override fun convert(value: String, element: AnnotatedElement?) = value

    override fun canConvertTo(targetType: Class<*>): Boolean {
        val isJavaObjectType = String::class.javaObjectType == targetType
        val isKotlinType = String::class.java == targetType
        return isJavaObjectType || isKotlinType
    }

}
