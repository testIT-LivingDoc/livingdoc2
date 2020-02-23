package org.livingdoc.jvm.engine.extension.context

import org.livingdoc.jvm.api.extension.context.DocumentFixtureContext
import org.livingdoc.jvm.api.extension.context.ExtensionContext
import org.livingdoc.jvm.api.extension.context.FixtureContext
import kotlin.reflect.KClass

internal class FixtureContextImpl(
    override val fixtureClass: KClass<*>,
    override val documentFixtureContext: DocumentFixtureContext
) : ContextImpl<ExtensionContext>(),
    FixtureContext {

    override val parent: DocumentFixtureContext?
        get() = documentFixtureContext

    override val testClass: KClass<*> get() = fixtureClass
}
