package org.livingdoc.jvm.extension

interface Context {
    val parent: Context?
    fun getStore(namespace: String): Store
}

/**
 * Store provides methods for extensions to save and retrieve data.
 * All read operations will consider the ancestors of this Store if the value was not found in this Store.
 */
interface Store {
    fun get(key: Any): Any?
    fun getOrComputeIfAbsent(key: Any, defaultCreator: (Any) -> Any): Any
    fun getListCombineAncestors(key: Any): List<*>
    fun put(key: Any, value: Any)
    fun remove(key: Any): Any?
}

fun Store.getOrDefault(key: Any, default: Any): Any = get(key) ?: default
