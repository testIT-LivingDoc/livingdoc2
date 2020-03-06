package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.Context
import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.TypeConverters.convertType
import org.livingdoc.converters.collection.Tokenizer.tokenizeToMap
import kotlin.reflect.KType

open class MapConverter : TypeConverter<Map<*, *>> {

    private val keyParameter: Int = 0
    private val valueParameter: Int = 1

    @Throws(ConversionException::class)
    override fun convert(value: String, type: KType, context: Context): Map<*, *> {
        val keyType = type.arguments.getOrNull(keyParameter)?.type ?: error("Bad type parameter: $type")
        val valueType = type.arguments.getOrNull(valueParameter)?.type ?: error("Bad type parameter: $type")
        val pairs = tokenizeToMap(value)
        return pairs.map { (key, value) ->
            val convertedKey = convertType(key, keyType, context)
            val convertedValue = convertType(value, valueType, context)
            convertedKey to convertedValue
        }.toMap()
    }

    override fun canConvertTo(targetType: Class<*>) = Map::class.java == targetType
}
