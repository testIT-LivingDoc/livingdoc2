package org.livingdoc.jvm.api.extension.context

import kotlin.reflect.KClass

interface ExtensionContext : Context<ExtensionContext> {
    val testClass: KClass<*>
}
