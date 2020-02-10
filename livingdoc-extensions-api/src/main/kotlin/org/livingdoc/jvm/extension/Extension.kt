package org.livingdoc.jvm.extension

import kotlin.reflect.KClass

annotation class Extension(
    val extensionClass: KClass<*>
)
