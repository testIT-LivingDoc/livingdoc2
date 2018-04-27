package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.TypeConverters.findTypeConverterForGenericElement
import org.livingdoc.converters.collection.Tokenizer.tokenizeToStringList
import java.lang.reflect.AnnotatedElement

abstract class AbstractCollectionConverter<T : Collection<Any>> : TypeConverter<T> {

    private val PARAM_INDEX = 0

    @Throws(ConversionException::class)
    override fun convert(value: String, element: AnnotatedElement, documentClass: Class<*>?): T {
        val converter = findTypeConverterForGenericElement(element, PARAM_INDEX, documentClass)
        val convertedValues = tokenizeToStringList(value)
            .map { converter.convert(it, element, documentClass) }
        return convertToTarget(convertedValues)
    }

    abstract fun convertToTarget(collection: List<Any>): T
}
