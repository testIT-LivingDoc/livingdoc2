package org.livingdoc.jvm.api.extension.context

import kotlin.reflect.KClass

interface GroupContext : ExtensionContext {
    val groupClass: KClass<*>
}
