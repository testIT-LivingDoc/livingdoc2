package org.livingdoc.jvm.engine

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

val KClass<*>.outer: KClass<*>?
    get() = this.java.declaringClass?.kotlin

fun <T : Any> KClass<*>.castToClass(base: KClass<T>): KClass<T> {
    if (this.isSubclassOf(base)) {
        return this as KClass<T>
    } else {
        throw IllegalArgumentException(
            "Can't cast ${this.qualifiedName} to ${base.qualifiedName}, because it's not a subclass"
        )
    }
}
