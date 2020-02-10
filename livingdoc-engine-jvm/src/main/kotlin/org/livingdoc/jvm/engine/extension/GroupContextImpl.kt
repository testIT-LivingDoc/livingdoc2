package org.livingdoc.jvm.engine.extension

import org.livingdoc.jvm.extension.Context
import org.livingdoc.jvm.extension.GroupContext
import kotlin.reflect.KClass

class GroupContextImpl(override val groupClass: KClass<*>) : GroupContext {
    override val parent: Context? = null

    private var stores = mutableMapOf<String, MutableMap<Any, Any>>()

    override fun getStore(namespace: String) = stores.getOrPut(namespace) { mutableMapOf() }
}
