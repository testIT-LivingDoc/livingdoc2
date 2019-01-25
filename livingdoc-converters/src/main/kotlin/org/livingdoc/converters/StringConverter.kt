package org.livingdoc.converters

import java.lang.reflect.AnnotatedElement
import org.livingdoc.api.conversion.TypeConverter

open class StringConverter : TypeConverter<String> {

    override fun convert(value: String, element: AnnotatedElement?, documentClass: Class<*>?): String = value

    override fun canConvertTo(targetType: Class<*>) = String::class.java == targetType
}
