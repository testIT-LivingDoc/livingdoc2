package org.livingdoc.jvm.api.extension.context

import kotlin.reflect.KClass

interface DocumentFixtureContext : ExtensionContext {
    val documentFixtureClass: KClass<*>
    val groupContext: GroupContext
}
