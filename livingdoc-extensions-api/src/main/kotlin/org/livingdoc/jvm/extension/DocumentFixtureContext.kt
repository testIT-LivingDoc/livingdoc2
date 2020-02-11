package org.livingdoc.jvm.extension

import kotlin.reflect.KClass

interface DocumentFixtureContext : ExtensionContext {
    val documentFixtureClass: KClass<*>
    val groupContext: GroupContext
}
