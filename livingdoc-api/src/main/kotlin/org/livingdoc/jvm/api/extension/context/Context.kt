package org.livingdoc.jvm.api.extension.context

interface Context<T : Context<T>> {
    val parent: T?
    fun getStore(namespace: String): Store
}
