package org.livingdoc.engine.reflection

import org.livingdoc.fixture.api.converter.Converter
import org.livingdoc.fixture.api.converter.TypeConverter
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

        findConverterFor(converting = type, from = parameter)?.let { return it }
        findConverterFor(converting = type, from = executable)?.let { return it }
        findConverterFor(converting = type, from = clazz)?.let { return it }

        documentClass?.run {
            findConverterFor(converting = type, from = documentClass)?.let { return it }
        }

        return findDefaultConverterFor(type)
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

        findConverterFor(converting = type, from = field)?.let { return it }
        findConverterFor(converting = type, from = clazz)?.let { return it }

        documentClass?.run {
            findConverterFor(converting = type, from = documentClass)?.let { return it }
        }

        return findDefaultConverterFor(type)
    }

    private fun findConverterFor(converting: Class<*>, from: AnnotatedElement): TypeConverter<*>? {
        val annotations = from.getAnnotationsByType(Converter::class.java)
        return annotations.toList().stream()
                .flatMap { it.value.toList().stream() }
                .map { TypeConverterManager.getInstance(it) }
                .filter { it.canConvertTo(converting) }
                .findFirst()
                .orElse(null)
    }

    private fun findDefaultConverterFor(type: Class<*>?): TypeConverter<out Any>? {
        return TypeConverterManager.getDefaultConverters().stream()
                .filter { it.canConvertTo(type) }
                .findFirst()
                .orElse(null)
    }

}