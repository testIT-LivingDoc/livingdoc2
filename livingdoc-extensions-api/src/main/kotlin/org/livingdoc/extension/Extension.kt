package org.livingdoc.extension

import kotlin.reflect.KClass

annotation class Extension(
    val extensionClass: KClass<*>
)
