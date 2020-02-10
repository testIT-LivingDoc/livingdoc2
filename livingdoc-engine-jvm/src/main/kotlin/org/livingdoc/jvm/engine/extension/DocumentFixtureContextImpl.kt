package org.livingdoc.jvm.engine.extension

import org.livingdoc.jvm.extension.Context
import org.livingdoc.jvm.extension.DocumentFixtureContext
import org.livingdoc.jvm.extension.GroupContext
import kotlin.reflect.KClass

class DocumentFixtureContextImpl(
    override val documentFixtureClass: KClass<*>,
    override val groupContext: GroupContext
) : DocumentFixtureContext {

    override val parent: Context?
        get() = groupContext

    private var stores = mutableMapOf<String, MutableMap<Any, Any>>()

    override fun getStore(namespace: String) = stores.getOrPut(namespace) { mutableMapOf() }
}
