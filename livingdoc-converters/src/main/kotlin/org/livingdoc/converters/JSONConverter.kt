package org.livingdoc.converters

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import org.livingdoc.api.conversion.Context
import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.TypeConverter
import java.io.StringReader
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * A TypeConverter to convert json strings into custom Types. This is not a Default TypeConverter and must always be
 * explicitly specified to be used.
 */
class JSONConverter<T> : TypeConverter<T> {

    override fun convert(value: String, type: KType, context: Context): T {
        val typeClass = getType(type)
        val klaxon = Klaxon()
        val json = try {
            klaxon.parser(typeClass).parse(StringReader(value)) as? JsonObject
                ?: throw ConversionException("Only json objects con be converted: $value")
        } catch (e: KlaxonException) {
            throw ConversionException("Can not parse json string '$value'", e)
        } catch (e: IllegalStateException) {
            // unhandled exception from the lexer
            throw ConversionException("Can not parse json string '$value'", e)
        }
        try {
            return klaxon.fromJsonObject(json, typeClass.java, typeClass) as T
        } catch (e: KlaxonException) {
            throw ConversionException(
                "Can not create object of type '$typeClass' from json '${json.toJsonString(
                    prettyPrint = true
                )}'", e
            )
        }
    }

    private fun getType(element: KType): KClass<*> {
        return element.classifier as KClass<*>
    }

    /**
     * This type converter can convert json to every target type.
     */
    override fun canConvertTo(targetType: KClass<*>) = true
}
