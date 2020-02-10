package org.livingdoc.jvm.extension

interface Context {
    val parent: Context?
    fun getStore(namespace: String): Store
}

typealias Store = MutableMap<Any, Any>
