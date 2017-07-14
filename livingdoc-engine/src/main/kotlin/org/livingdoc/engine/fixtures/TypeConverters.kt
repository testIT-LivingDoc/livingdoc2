package org.livingdoc.engine.fixtures

import org.livingdoc.api.conversion.Converter
import org.livingdoc.api.conversion.TypeConverter
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import java.lang.reflect.Parameter

/**
 * Utility object providing [TypeConverter] related operations.
 */
internal object TypeConverters {

    // TODO for future development
    // - inheritance >> loading converters from annotations down the inheritance stack (class level only)
    // - interfaces >> loading converters from annotations on implemented interfaces (class level only)
    // - nested classes >> loading converters from declaring class?

    /**
     * Tries to find a [TypeConverter] for the given [Parameter].
     *
     * Type converters can be configured by annotating either the parameter, the parameter's method, the fixture class
     * or the document binding class with [Converter]. This is also the order in which this method searches for a matching
     * type converter:
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
                .filter { it.canConvertTo(converting) }
                .firstOrNull()
    }

    private fun findDefaultConverterFor(type: Class<*>?): TypeConverter<out Any>? {
        return TypeConverterManager.getDefaultConverters()
                .filter { it.canConvertTo(type) }
                .firstOrNull()
    }

}