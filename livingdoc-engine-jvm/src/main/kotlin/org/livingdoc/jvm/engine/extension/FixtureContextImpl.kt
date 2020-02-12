package org.livingdoc.jvm.engine.extension

import org.livingdoc.jvm.extension.DocumentFixtureContext
import org.livingdoc.jvm.extension.ExtensionContext
import org.livingdoc.jvm.extension.FixtureContext
import kotlin.reflect.KClass

internal class FixtureContextImpl(
    override val fixtureClass: KClass<*>,
    override val documentFixtureContext: DocumentFixtureContext
) : ContextImpl<ExtensionContext>(), FixtureContext {

    override val parent: DocumentFixtureContext?
        get() = documentFixtureContext

    override val testClass: KClass<*> get() = fixtureClass
}
