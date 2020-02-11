package org.livingdoc.jvm.extension

import kotlin.reflect.KClass

interface GroupContext : ExtensionContext {
    val groupClass: KClass<*>
}
