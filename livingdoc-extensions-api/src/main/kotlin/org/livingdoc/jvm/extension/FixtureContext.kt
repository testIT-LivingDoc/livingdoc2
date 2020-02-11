package org.livingdoc.jvm.extension

import kotlin.reflect.KClass

interface FixtureContext : ExtensionContext {
    val fixtureClass: KClass<*>
    val documentFixtureContext: DocumentFixtureContext
}
