package org.livingdoc.jvm.engine

import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation
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

/**
 * Calls all jvm static or companion object functions annotated with the given annotation. The functions must not take
 * any parameter and don't return anything.
 * Throws an exception if any annotated function take an argument.
 */
inline fun <reified T : Annotation> KClass<*>.callStaticAnnotatedFunctionWithNoArguments() {
    companionObject?.declaredFunctions.orEmpty()
        .filter { it.hasAnnotation<T>() }
        .forEach { it.call(companionObjectInstance) }
}
