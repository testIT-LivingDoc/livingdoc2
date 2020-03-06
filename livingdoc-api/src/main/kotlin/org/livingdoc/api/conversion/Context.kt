package org.livingdoc.api.conversion

import kotlin.reflect.KAnnotatedElement

/**
 * Context contains all information needed to final all TypeConverters.
 */
data class Context(
    val element: KAnnotatedElement,
    val parent: Context?
)
