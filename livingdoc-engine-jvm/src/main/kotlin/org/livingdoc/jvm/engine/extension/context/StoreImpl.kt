package org.livingdoc.jvm.engine.extension.context

import org.livingdoc.jvm.api.extension.context.Store

internal class StoreImpl(
    getAncestor: () -> Store?
) : Store {
    private val map: MutableMap<Any, Any> = mutableMapOf()
    private val ancestor: Store? by lazy(getAncestor)

    override fun get(key: Any): Any? = map[key] ?: ancestor?.get(key)

    override fun getOrComputeIfAbsent(key: Any, defaultCreator: (Any) -> Any): Any =
        get(key) ?: map.computeIfAbsent(key, defaultCreator)

    override fun getListCombineAncestors(key: Any): List<*> =
        ancestor?.getListCombineAncestors(key).orEmpty() + (map[key] as? List<*>).orEmpty()

    override fun put(key: Any, value: Any) {
        map[key] = value
    }

    override fun remove(key: Any): Any? = map.remove(key)
}
