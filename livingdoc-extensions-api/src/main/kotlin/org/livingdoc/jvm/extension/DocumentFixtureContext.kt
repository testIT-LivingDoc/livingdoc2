package org.livingdoc.jvm.extension

import kotlin.reflect.KClass

interface DocumentFixtureContext : Context {
    val documentFixtureClass: KClass<*>
    val groupContext: GroupContext
}
