package org.livingdoc.api.documents

import kotlin.reflect.KClass

/**
 * This annotation is used to mark a document to use fail fast behaviour. Whenever a test fails, the whole execution
 * should stop.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FailFast(
    /**
     * The Array of Exceptions that should lead to a fast fail
     */
    val onExceptionTypes: Array<KClass<out Throwable>> = [Throwable::class]
)
