package org.livingdoc.converters

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import org.livingdoc.api.conversion.TypeConverter
import java.io.StringReader
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import java.lang.reflect.Parameter

/**
 * A TypeConverter to convert json strings into custom Types. This is not a Default TypeConverter and must always be
 * explicitly specified to be used.
 */
class JSONConverter<T> : TypeConverter<T> {

    override fun convert(value: String, element: AnnotatedElement?, documentClass: Class<*>?): T {
        val typeClass = getType(element ?: throw IllegalArgumentException("The element must be given"))
        val klaxon = Klaxon()
        val json = klaxon.parser(typeClass.kotlin).parse(StringReader(value)) as JsonObject
        return klaxon.fromJsonObject(json, typeClass, typeClass.kotlin) as T
    }

    private fun getType(element: AnnotatedElement): Class<*> {
        return when (element) {
            is Field -> element.type
            is Parameter -> element.type
            else -> error("annotated element is of a not supported type: $element")
        } ?: throw TypeConverters.NoTypeConverterFoundException(element)
    }

    /**
     * This type converter can convert json to every target type.
     */
    override fun canConvertTo(targetType: Class<*>) = true
}
