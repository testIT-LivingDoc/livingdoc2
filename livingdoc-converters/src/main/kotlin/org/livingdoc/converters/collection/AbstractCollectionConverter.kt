package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.TypeConverter
import org.livingdoc.converters.TypeConverters
import java.lang.reflect.*

abstract class AbstractCollectionConverter<T : Collection<Any>> : TypeConverter<T> {

    val defaultSeparator = ","

    @Throws(ConversionException::class)
    override fun convert(value: String, element: AnnotatedElement?, documentClass: Class<*>?): T {
        val tokenized: List<String> = tokenize(value)
        val paramsConverted = convertIntoParameterType(element, documentClass, tokenized)

        return convertToTarget(paramsConverted)
    }

    private fun tokenize(value: String): List<String> {
        return value.split(delimiters = defaultSeparator).map { it.trim() }
    }

    private fun convertIntoParameterType(element: AnnotatedElement?, documentClass: Class<*>?, tokenized: List<String>): List<Any> {

        val paramTypeConverter = when (element) {
            is Field -> {
                converterFromField(element, documentClass)
            }
            is Parameter -> {
                converterFromParameter(element, documentClass)
            }
            else -> null
        } ?: throw IllegalStateException()

        return tokenized.map { paramTypeConverter.convert(it, element, documentClass) }.toList()
    }

    private fun converterFromParameter(parameter: Parameter, documentClass: Class<*>?): TypeConverter<*>? {
        val targetType = getTargetType(parameter.parameterizedType)
        return TypeConverters.findTypeConverter(targetType as Class<*>, parameter, documentClass)
    }

    private fun converterFromField(field: Field, documentClass: Class<*>?): TypeConverter<*>? {
        val targetType = getTargetType(field.genericType)
        return TypeConverters.findTypeConverter(targetType as Class<*>, field, documentClass)
    }

    private fun getTargetType(type: Type): Type {
        val parameterizedType = type as ParameterizedType

        val actualTypeArguments = parameterizedType.actualTypeArguments
        return actualTypeArguments!![0]
    }

    abstract fun convertToTarget(collection: List<Any>): T
}
