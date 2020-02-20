package org.livingdoc.jvm.engine.manager

import org.livingdoc.jvm.api.extension.Extension
import org.livingdoc.jvm.engine.castToClass
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

internal fun loadExtensions(testClass: KClass<*>): List<Extension> {
    return testClass.findAnnotation<org.livingdoc.api.Extensions>()?.value.orEmpty().toList()
        .map { instantiateExtension(it) }
}

/**
 * Create new instances of the extension class.
 */
private fun instantiateExtension(extensionClass: KClass<*>): Extension {
    return extensionClass.castToClass(Extension::class).createInstance()
}
