package org.livingdoc.jvm.engine.extension

import org.livingdoc.jvm.extension.Context
import org.livingdoc.jvm.extension.Store

open class ContextImpl(override val parent: Context? = null) : Context {
    private var stores = mutableMapOf<String, StoreImpl>()

    override fun getStore(namespace: String) = stores.getOrPut(namespace) { StoreImpl(namespace, this) }
}

class StoreImpl(
    private val namespace: String,
    private val context: ContextImpl
) : Store {
    private val map: MutableMap<Any, Any> = mutableMapOf()
    private val ancestor: StoreImpl?
        get() = context.parent?.getStore(namespace) as StoreImpl?

    override fun get(key: Any): Any? = map[key] ?: ancestor?.get(key)

    override fun getOrComputeIfAbsent(key: Any, defaultCreator: (Any) -> Any): Any =
        get(key) ?: map.computeIfAbsent(key, defaultCreator)

    override fun getListCombineAncestors(key: Any): List<*> =
        (map[key] as? List<*>).orEmpty() + ancestor?.getListCombineAncestors(key).orEmpty()

    override fun put(key: Any, value: Any) {
        map[key] = value
    }

    override fun remove(key: Any): Any? = map.remove(key)
}
