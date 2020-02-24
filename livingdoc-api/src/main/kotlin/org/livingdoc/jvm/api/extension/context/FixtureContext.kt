package org.livingdoc.jvm.api.extension.context

import kotlin.reflect.KClass

interface FixtureContext : ExtensionContext {
    val fixtureClass: KClass<*>
    val documentFixtureContext: DocumentFixtureContext
}
