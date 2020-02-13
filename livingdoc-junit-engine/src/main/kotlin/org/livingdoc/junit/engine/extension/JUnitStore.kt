package org.livingdoc.junit.engine.extension

import org.junit.jupiter.api.extension.ExtensionContext
import org.livingdoc.jvm.api.extension.context.Store
import java.util.function.Function

class JUnitStore(private val store: Store) :
    ExtensionContext.Store {
    override fun <K : Any, V : Any> getOrComputeIfAbsent(key: K, defaultCreator: Function<K, V>): Any {
        return store.getOrComputeIfAbsent(key) { defaultCreator }
    }

    override fun <K : Any, V : Any> getOrComputeIfAbsent(
        key: K,
        defaultCreator: Function<K, V>,
        requiredType: Class<V>
    ): V {
        return requiredType.cast(getOrComputeIfAbsent(key, defaultCreator))
    }

    override fun put(key: Any, value: Any?) {
        value ?: throw IllegalArgumentException("Null values are not supported by this Store implementation")
        return store.put(key, value)
    }

    override fun remove(key: Any): Any? {
        return store.remove(key)
    }

    override fun <V : Any> remove(key: Any, requiredType: Class<V>): V? {
        return requiredType.cast(remove(key))
    }

    override fun get(key: Any): Any? {
        return store.get(key)
    }

    override fun <V : Any> get(key: Any, requiredType: Class<V>): V? {
        return requiredType.cast(get(key))
    }
}
