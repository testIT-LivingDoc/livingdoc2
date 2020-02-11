package org.livingdoc.jvm.extension

import kotlin.reflect.KClass

interface ExtensionContext : Context<ExtensionContext> {
    val testClass: KClass<*>
}
