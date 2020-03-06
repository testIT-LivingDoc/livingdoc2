package org.livingdoc.converters

import io.mockk.every
import io.mockk.mockk
import org.livingdoc.api.conversion.Context
import org.livingdoc.api.conversion.TypeConverter
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty

fun <T> TypeConverter<T>.convertValueOnly(value: String): T {
    return this.convert(value, mockk(), contextWithAnnotation(emptyList()))
}

fun <T> TypeConverter<T>.convertValueForParameter(value: String, parameter: KParameter): T {
    return this.convert(value, parameter.type, Context(parameter, null))
}

fun <T> TypeConverter<T>.convertValueForProperty(value: String, property: KProperty<*>): T {
    return this.convert(value, property.returnType,
        Context(property, null)
    )
}

/**
 * Create context with an element that have the given annotations. The Context has no parent context.
 */
internal fun contextWithAnnotation(annotations: List<Annotation>): Context {
    val element: KAnnotatedElement = mockk()
    every { element.annotations } returns annotations
    return Context(element, null)
}
