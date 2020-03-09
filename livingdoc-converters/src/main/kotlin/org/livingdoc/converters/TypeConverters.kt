package org.livingdoc.converters

import org.livingdoc.api.conversion.Context
import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.Converter
import org.livingdoc.api.conversion.TypeConverter
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Utility object providing [TypeConverter] related operations.
 */
object TypeConverters {

    /**
     * Convert a given String [value] to the [type] using the [context] to search Type Converter.
     */
    fun convertStringToType(value: String, type: KType, context: Context): Any? {
        return findTypeConverter(value, type, context).convert(value, type, context)
    }

    internal fun findTypeConverter(value: String, type: KType, context: Context): TypeConverter<*> {
        val classifier = type.classifier ?: throw IllegalArgumentException("Given type has no classifier: $type")
        classifier as KClass<*>
        return findTypeConverterRecursive(value, classifier, context) ?: throw NoTypeConverterFoundException(type)
    }

    private fun findTypeConverterRecursive(value: String, type: KClass<*>, context: Context): TypeConverter<*>? {
        val annotations = context.element.annotations.filterIsInstance<Converter>()
        var typeConverter = annotations.flatMap { it.value.toList() }
            .map { TypeConverterManager.getInstance(it) }
            .firstOrNull { it.canConvertTo(type) }
        if (typeConverter == null) {
            val parent = context.parent
            typeConverter = if (parent != null) {
                findTypeConverterRecursive(value, type, parent)
            } else {
                findDefaultConverterFor(type)
            }
        }

        return typeConverter
    }

    private fun findDefaultConverterFor(type: KClass<*>): TypeConverter<*>? {
        return TypeConverterManager.getDefaultConverters()
            .firstOrNull { it.canConvertTo(type) }
    }

    internal class NoTypeConverterFoundException(annotatedElement: KAnnotatedElement) :
        ConversionException("No type converter could be found to convert annotated element: $annotatedElement")
}
