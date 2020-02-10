package org.livingdoc.jvm.engine.extension

import org.livingdoc.jvm.extension.Context
import org.livingdoc.jvm.extension.DocumentFixtureContext
import org.livingdoc.jvm.extension.FixtureContext
import kotlin.reflect.KClass

class FixtureContextImpl(
    override val fixtureClass: KClass<*>,
    override val documentFixtureContext: DocumentFixtureContext
) : FixtureContext {

    override val parent: Context?
        get() = documentFixtureContext

    private var stores = mutableMapOf<String, MutableMap<Any, Any>>()

    override fun getStore(namespace: String) = stores.getOrPut(namespace) { mutableMapOf() }
}
