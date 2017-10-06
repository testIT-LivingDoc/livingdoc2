package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.TypeConverters
import java.lang.reflect.*

abstract class AbstractCollectionConverter {

    val defaultSeparator = ","
    var documentClass: Class<*>? = null
    lateinit var element: AnnotatedElement

    internal fun tokenize(value: String, delimiter: String): List<String> {
        return value.split(delimiters = delimiter).map { it.trim() }
    }

    internal fun convertToTypedParam(converter: TypeConverter<*>, tokenized: List<String>): List<Any> {
        return tokenized.map { converter.convert(it, element, documentClass) }.toList()
    }

    internal fun findTypeConverterForElement(parameterIndex: Int): TypeConverter<*> {
        return when (element) {
            is Field -> findTypeConverterForTypedParam(element as Field, parameterIndex)
            is Parameter -> findTypeConverterForTypedParam(element as Parameter, parameterIndex)
            else -> error("annotated element is of a not supported type: $element")
        } ?: throw NoTypeConverterFoundException(element)
    }

    private fun findTypeConverterForTypedParam(parameter: Parameter, parameterIndex: Int): TypeConverter<*>? {
        val targetType = getTargetType(parameter.parameterizedType, parameterIndex)
        return TypeConverters.findTypeConverter(targetType as Class<*>, parameter, documentClass)
    }

    private fun findTypeConverterForTypedParam(field: Field, parameterIndex: Int): TypeConverter<*>? {
        val targetType = getTargetType(field.genericType, parameterIndex)
        return TypeConverters.findTypeConverter(targetType as Class<*>, field, documentClass)
    }

    private fun getTargetType(type: Type, parameterIndex: Int): Type {
        val parametrizedType = type as ParameterizedType
        val actualTypeArguments = parametrizedType.actualTypeArguments
        return actualTypeArguments[parameterIndex]
    }

    internal fun setElementAndDocument(documentClass: Class<*>?, element: AnnotatedElement) {
        this.documentClass = documentClass
        this.element = element
    }


    internal class NoTypeConverterFoundException(annotatedElement: AnnotatedElement)
        : ConversionException("No type converter could be found to convert annotated element: $annotatedElement")
}
