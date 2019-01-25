package org.livingdoc.converters

import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.Converter
import org.livingdoc.api.conversion.TypeConverter
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import java.lang.reflect.Parameter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Utility object providing [TypeConverter] related operations.
 */
object TypeConverters {

    // TODO for future development
    // - inheritance >> loading converters from annotations down the inheritance stack (class level only)
    // - interfaces >> loading converters from annotations on implemented interfaces (class level only)
    // - nested classes >> loading converters from declaring class?

    /**
     * Tries to find a [TypeConverter] for the [ParameterizedType]s appropriate for
     * the given generic [AnnotatedElement].
     *
     * Type converters are found by either analyzing the given [Field] or [Parameter] for
     * its [ParameterizedType]s, delegating the search for the appropriate [TypeConverter]
     * of the found [ParameterizedType] to [findTypeConverter].
     *
     * 1. check if inferred annotated element is field or parameter
     * 2. get the target type by extracting the actualTypeArguments
     * 3. call the appropriate method
     *
     * @param element the annotated or inferred element
     * @param parameterIndex the index for the parameterized type for which we search the typeconverter
     * @param documentClass the (optional) document class
     * @exception error if the element is neither Field nor Parameter
     * @exception NoTypeConverterFoundException if no appropriate typeconverter could be found
     * @return the found type converter
     */
    fun findTypeConverterForGenericElement(
        element: AnnotatedElement,
        parameterIndex: Int,
        documentClass: Class<*>?
    ): TypeConverter<*> {
        return when (element) {
            is Field -> {
                val targetType = getTargetType(element.genericType, parameterIndex)
                findTypeConverter(targetType as Class<*>, element, documentClass)
            }
            is Parameter -> {
                val targetType = getTargetType(element.parameterizedType, parameterIndex)
                findTypeConverter(targetType as Class<*>, element, documentClass)
            }
            else -> error("annotated element is of a not supported type: $element")
        } ?: throw NoTypeConverterFoundException(element)
    }

    private fun getTargetType(type: Type, parameterIndex: Int): Type {
        type as ParameterizedType
        val actualTypeArguments = type.actualTypeArguments
        return actualTypeArguments[parameterIndex]
    }

    internal class NoTypeConverterFoundException(annotatedElement: AnnotatedElement) :
        ConversionException("No type converter could be found to convert annotated element: $annotatedElement")

    /**
     * Tries to find a [TypeConverter] for the given [Parameter].
     *
     * Type converters can be configured by annotating either the parameter, the parameter's method,
     * the fixture class or the document binding class with [Converter]. This is also the order in
     * which this method searches for a matching type converter:
     *
     * 1. check for parameter annotations
     * 2. check for method annotations
     * 3. check for class annotations
     * 4. check (optional) document class annotations
     * 5. check default type converter list
     *
     * @param parameter the parameter
     * @param documentClass the (optional) document class
     * @return the found type converter or `null` if no matching converter was found
     */
    fun findTypeConverter(parameter: Parameter, documentClass: Class<*>? = null): TypeConverter<*>? {
        val type = parameter.type
        return findTypeConverter(type, parameter, documentClass)
    }

    private fun findTypeConverter(type: Class<*>, parameter: Parameter, documentClass: Class<*>?): TypeConverter<*>? {
        val executable = parameter.declaringExecutable
        val clazz = executable.declaringClass
        return findConverterFor(converting = type, from = parameter)
            ?: findConverterFor(converting = type, from = executable)
            ?: findConverterFor(converting = type, from = clazz)
            ?: documentClass?.let { findConverterFor(converting = type, from = documentClass) }
            ?: findDefaultConverterFor(type)
    }

    /**
     * Tries to find a [TypeConverter] for the given [Field].
     *
     * Type converters can be configured by annotating either the field, the fixture class or the document binding class
     * with [Converter]. This is also the order in which this method searches for a matching type converter:
     *
     * 1. check for field annotations
     * 2. check for class annotations
     * 3. check (optional) document class annotations
     * 4. check default type converter list
     *
     * @param field the field
     * @param documentClass the (optional) document class
     * @return the found type converter or `null` if no matching converter was found
     */
    fun findTypeConverter(field: Field, documentClass: Class<*>? = null): TypeConverter<*>? {
        val type = field.type
        return findTypeConverter(type, field, documentClass)
    }

    private fun findTypeConverter(type: Class<*>, field: Field, documentClass: Class<*>?): TypeConverter<*>? {
        val clazz = field.declaringClass
        return findConverterFor(converting = type, from = field)
            ?: findConverterFor(converting = type, from = clazz)
            ?: documentClass?.let { findConverterFor(converting = type, from = documentClass) }
            ?: findDefaultConverterFor(type)
    }

    private fun findConverterFor(converting: Class<*>, from: AnnotatedElement): TypeConverter<*>? {
        val annotations = from.getAnnotationsByType(Converter::class.java)
        return annotations.toList()
            .flatMap { it.value.toList() }
            .map { TypeConverterManager.getInstance(it) }
            .firstOrNull { it.canConvertTo(converting) }
    }

    private fun findDefaultConverterFor(type: Class<*>?): TypeConverter<out Any>? {
        return TypeConverterManager.getDefaultConverters()
            .firstOrNull { it.canConvertTo(type) }
    }
}
