package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.TypeConverters.findTypeConverterForGenericElement
import org.livingdoc.converters.collection.Tokenizer.tokenizeToMap
import java.lang.reflect.AnnotatedElement

open class MapConverter : TypeConverter<Map<Any, Any>> {

    private val KEY_INDEX: Int = 0
    private val VALUE_INDEX: Int = 1

    @Throws(ConversionException::class)
    override fun convert(value: String, element: AnnotatedElement, documentClass: Class<*>?): Map<Any, Any> {
        val keyConverter = findTypeConverterForGenericElement(element, KEY_INDEX, documentClass)
        val valueConverter = findTypeConverterForGenericElement(element, VALUE_INDEX, documentClass)
        val pairs = tokenizeToMap(value)
        return pairs.map { (key, value) ->
            val convertedKey = keyConverter.convert(key, element, documentClass)
            val convertedValue = valueConverter.convert(value, element, documentClass)
            convertedKey to convertedValue
        }.toMap()
    }

    override fun canConvertTo(targetType: Class<*>?) = Map::class.java == targetType
}
