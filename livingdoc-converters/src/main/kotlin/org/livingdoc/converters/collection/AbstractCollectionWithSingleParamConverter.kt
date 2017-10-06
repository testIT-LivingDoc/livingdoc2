package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.TypeConverter
import java.lang.reflect.AnnotatedElement

abstract class AbstractCollectionWithSingleParamConverter<T : Collection<Any>>: AbstractCollectionConverter(), TypeConverter<T> {

    private val PARAM_INDEX = 0

    @Throws(ConversionException::class)
    override fun convert(value: String, element: AnnotatedElement, documentClass: Class<*>?): T {
        setElementAndDocument(documentClass, element)

        val converter = findTypeConverterForElement(PARAM_INDEX)
        val tokens: List<String> = tokenize(value, defaultSeparator)
        val convertedValues = convertToTypedParam(converter, tokens)
        return convertToTarget(convertedValues)
    }

    abstract fun convertToTarget(collection: List<Any>): T
}
