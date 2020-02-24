package org.livingdoc.jvm.engine.manager

import org.livingdoc.jvm.api.extension.Extension
import kotlin.reflect.KClass

interface ExtensionRegistry {
    /**
     * Get all Extensions with the given type in the current context.
     */
    fun <T : Extension> getExtensions(type: KClass<T>): List<T>

    /**
     * Get all Extensions with the given type in the current context in reverse order. This should be used to invoke the
     * extensions after the test execution.
     */
    fun <T : Extension> getExtensionsReverse(type: KClass<T>): List<T> = getExtensions(type).reversed()
}
