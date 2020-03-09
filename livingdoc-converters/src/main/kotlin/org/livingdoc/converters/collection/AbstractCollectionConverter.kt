package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.Context
import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.TypeConverters.convertStringToType
import org.livingdoc.converters.collection.Tokenizer.tokenizeToStringList
import kotlin.reflect.KType

abstract class AbstractCollectionConverter<T : Collection<*>> : TypeConverter<T> {

    private val firstParameter = 0

    @Throws(ConversionException::class)
    override fun convert(value: String, type: KType, context: Context): T {
        val typeArgument = type.arguments.getOrNull(0)?.type ?: error("Bad type argument: $type")

        val convertedValues = tokenizeToStringList(value)
            .map { convertStringToType(it, typeArgument, context) }
        return convertToTarget(convertedValues)
    }

    abstract fun convertToTarget(collection: List<*>): T
}
