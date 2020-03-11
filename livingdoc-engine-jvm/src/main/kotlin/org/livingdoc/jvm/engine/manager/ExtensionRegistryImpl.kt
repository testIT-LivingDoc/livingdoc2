package org.livingdoc.jvm.engine.manager

import org.livingdoc.jvm.api.extension.Extension
import org.livingdoc.jvm.engine.castToClass
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

/**
 * A hierarchical ExtensionRegistry, which can have a parent. If it has a parent the extensions form the parent are
 * included in this ExtensionRegistry in the correct order.
 */
internal class ExtensionRegistryImpl(private val extensions: List<Extension>, private val parent: ExtensionRegistry?) :
    ExtensionRegistry {
    override fun <T : Extension> getExtensions(type: KClass<T>): List<T> {
        return parent?.getExtensions(type).orEmpty() + extensions.filterIsInstance(type.java)
    }

    companion object {
        private val defaultExtensions = ServiceLoader.load(Extension::class.java).iterator().asSequence().toList()

        fun createRootExtensionRegistry(): ExtensionRegistry {
            return ExtensionRegistryImpl(defaultExtensions, null)
        }

        fun createRegistryFrom(
            extensionTypes: List<KClass<Extension>>,
            parentRegistry: ExtensionRegistry
        ): ExtensionRegistry {
            return ExtensionRegistryImpl(extensionTypes.map { instantiateExtension(it) }, parentRegistry)
        }

        fun loadExtensions(testClass: KClass<*>): List<KClass<Extension>> {
            return testClass.findAnnotation<org.livingdoc.api.Extensions>()?.value.orEmpty().toList()
        }

        /**
         * Create new instances of the extension class.
         */
        private fun instantiateExtension(extensionClass: KClass<*>): Extension {
            return extensionClass.castToClass(Extension::class).createInstance()
        }
    }
}
