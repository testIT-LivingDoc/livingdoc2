package org.livingdoc.jvm.extension

interface Context<T : Context<T>> {
    val parent: T?
    fun getStore(namespace: String): Store
}
