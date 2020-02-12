package org.livingdoc.jvm.engine.extension

import org.livingdoc.jvm.extension.Context
import org.livingdoc.jvm.extension.Store

internal open class ContextImpl<T : Context<T>>(override val parent: T? = null) : Context<T> {
    private var stores = mutableMapOf<String, StoreImpl>()

    override fun getStore(namespace: String): Store =
        stores.getOrPut(namespace) { StoreImpl { this.parent?.getStore(namespace) } }
}

