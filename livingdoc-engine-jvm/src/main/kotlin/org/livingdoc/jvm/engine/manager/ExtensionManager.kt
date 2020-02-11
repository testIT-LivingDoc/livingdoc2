package org.livingdoc.jvm.engine.manager

import org.livingdoc.jvm.engine.castToClass
import org.livingdoc.jvm.extension.Context
import org.livingdoc.jvm.extension.DocumentFixtureContext
import org.livingdoc.jvm.extension.FixtureContext
import org.livingdoc.jvm.extension.GroupContext
import org.livingdoc.jvm.extension.Store
import org.livingdoc.jvm.extension.spi.CallbackExtension
import org.livingdoc.jvm.extension.spi.ExecutionCondition
import org.livingdoc.jvm.extension.spi.Extension
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

class ExtensionManager {

    private val defaultExtensions = ServiceLoader.load(Extension::class.java).iterator().asSequence().toList()

    /**
     * Get all Extensions form the context and the default Extensions
     */
    private fun getAllExtensions(context: Context): List<Extension> =
        context.extensionStore.extensions + defaultExtensions

    fun executeBeforeGroup(context: GroupContext) {
        val activeExtensions = defaultExtensions + context.extensionStore.extensions
        activeExtensions.extensionsOfType<CallbackExtension>().forEach {
            it.onBeforeGroup(context)
        }
    }

    fun executeBeforeDocumentFixture(context: DocumentFixtureContext) {
        val activeExtensions = defaultExtensions + context.extensionStore.extensions
        activeExtensions.extensionsOfType<CallbackExtension>().forEach {
            it.onBeforeDocument(context)
        }
    }

    fun executeBeforeFixture(context: FixtureContext) {
        val activeExtensions = defaultExtensions + context.extensionStore.extensions
        activeExtensions.extensionsOfType<CallbackExtension>().forEach {
            it.onBeforeFixture(context)
        }
    }

    fun executeAfterFixture(context: FixtureContext) {
        val activeExtensions = defaultExtensions + context.extensionStore.extensions
        activeExtensions.extensionsOfType<CallbackExtension>().forEach {
            it.onAfterFixture(context)
        }
    }

    fun executeAfterDocumentFixture(context: DocumentFixtureContext) {
        val activeExtensions = defaultExtensions + context.extensionStore.extensions
        activeExtensions.extensionsOfType<CallbackExtension>().forEach {
            it.onAfterDocument(context)
        }
    }

    fun executeAfterGroup(context: GroupContext) {
        val activeExtensions = defaultExtensions + context.extensionStore.extensions
        activeExtensions.extensionsOfType<CallbackExtension>().forEach {
            it.onAfterGroup(context)
        }
    }

    fun shouldExecute(context: Context): Boolean {
        return getAllExtensions(context).extensionsOfType<ExecutionCondition>()
            .map { it.evaluateExecutionCondition(context) }
            .all { it.enabled }
    }

    fun loadExtensions(context: GroupContext) {
        context.extensionStore.extensions = context.extensionClasses.map { instantiateExtension(it) }
    }

    fun loadExtensions(context: DocumentFixtureContext) {
        context.extensionStore.extensions = context.extensionClasses.map { instantiateExtension(it) }
    }

    fun loadExtensions(context: FixtureContext) {
        context.extensionStore.extensions = context.extensionClasses.map { instantiateExtension(it) }
    }
}

fun instantiateExtension(extensionClass: KClass<*>): CallbackExtension {
    return extensionClass.castToClass(CallbackExtension::class).createInstance()
}

private val Context.extensionStore: Store
    get() = this.getStore("org.livingdoc.jvm.engine.manager.ExtensionManager")

private val GroupContext.extensionClasses: List<KClass<*>>
    get() = this.groupClass.findAnnotation<org.livingdoc.api.Extensions>()?.value?.toList() ?: emptyList()

private val DocumentFixtureContext.extensionClasses: List<KClass<*>>
    get() = this.documentFixtureClass.findAnnotation<org.livingdoc.api.Extensions>()?.value?.toList() ?: emptyList()

private val FixtureContext.extensionClasses: List<KClass<*>>
    get() = this.fixtureClass.findAnnotation<org.livingdoc.api.Extensions>()?.value?.toList() ?: emptyList()

private var Store.extensions: List<Extension>
    get() = getListCombineAncestors("extensions").filterIsInstance<Extension>()
    set(value) {
        put("extensions", value)
    }

private inline fun <reified T : Extension> List<Extension>.extensionsOfType(): List<T> {
    return this.filterIsInstance<T>()
}
