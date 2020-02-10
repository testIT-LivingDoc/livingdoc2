package org.livingdoc.jvm.extension

import kotlin.reflect.KClass

interface FixtureContext : Context {
    val fixtureClass: KClass<*>
    val documentFixtureContext: DocumentFixtureContext
}
