package org.livingdoc.converters

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import org.livingdoc.api.conversion.ConversionException
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
        val json = try {
            klaxon.parser(typeClass.kotlin).parse(StringReader(value)) as? JsonObject
                ?: throw ConversionException("Only json objects con be converted: $value")
        } catch (e: KlaxonException) {
            throw ConversionException("Can not parse json string '$value'", e)
        } catch (e: IllegalStateException) {
            // unhandled exception from the lexer
            throw ConversionException("Can not parse json string '$value'", e)
        }
        try {
            return klaxon.fromJsonObject(json, typeClass, typeClass.kotlin) as T
        } catch (e: KlaxonException) {
            throw ConversionException(
                "Can not create object of type '$typeClass' from json '${json.toJsonString(
                    prettyPrint = true
                )}'", e
            )
        }
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
