package org.livingdoc.converters

import org.livingdoc.api.conversion.Context
import kotlin.reflect.KAnnotatedElement

inline fun <reified T : Annotation> Context.findAnnotation(): T? {
    val filter = { annotation: Annotation -> annotation is T }
    return filterAnnotations(filter) as T?
}

fun Context.filterAnnotations(filter: (Annotation) -> Boolean): Annotation? {
    return element.annotations.firstOrNull(filter) ?: parent?.filterAnnotations(filter)
}

fun Context.createContext(element: KAnnotatedElement): Context {
    return Context(element, this)
}

fun contextOf(rootElement: KAnnotatedElement, vararg elements: KAnnotatedElement): Context {
    return elements.fold(Context(rootElement, null)) { context: Context, element ->
        Context(element, context)
    }
}
