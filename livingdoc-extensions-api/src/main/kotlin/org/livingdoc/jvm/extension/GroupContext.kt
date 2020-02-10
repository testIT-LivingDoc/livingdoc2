package org.livingdoc.jvm.extension

import kotlin.reflect.KClass

interface GroupContext : Context {
    val groupClass: KClass<*>
}
