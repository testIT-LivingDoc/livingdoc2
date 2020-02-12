package org.livingdoc.jvm.engine.manager

import org.livingdoc.jvm.engine.castToClass
import org.livingdoc.jvm.extension.DocumentFixtureContext
import org.livingdoc.jvm.extension.ExtensionContext
import org.livingdoc.jvm.extension.FixtureContext
import org.livingdoc.jvm.extension.GroupContext
import org.livingdoc.jvm.extension.Store
import org.livingdoc.jvm.extension.spi.CallbackExtension
import org.livingdoc.jvm.extension.spi.ExecutionCondition
import org.livingdoc.jvm.extension.spi.Extension
import org.livingdoc.jvm.extension.spi.TestExecutionExceptionHandler
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

class ExtensionManager {

    private val defaultExtensions = ServiceLoader.load(Extension::class.java).iterator().asSequence().toList()

    /**
     * Get all Extensions form the context and the default Extensions
     */
    private fun getAllExtensions(context: ExtensionContext): List<Extension> =
        defaultExtensions + context.extensionStore.extensions

    fun executeBeforeGroup(context: GroupContext) {
        val activeExtensions = getAllExtensions(context)
        activeExtensions.extensionsOfType<CallbackExtension>().forEach {
            it.onBeforeGroup(context)
        }
    }

    fun executeBeforeDocumentFixture(context: DocumentFixtureContext) {
        val activeExtensions = getAllExtensions(context)
        activeExtensions.extensionsOfType<CallbackExtension>().forEach {
            it.onBeforeDocument(context)
        }
    }

    fun executeBeforeFixture(context: FixtureContext) {
        val activeExtensions = getAllExtensions(context)
        activeExtensions.extensionsOfType<CallbackExtension>().forEach {
            it.onBeforeFixture(context)
        }
    }

    fun executeAfterFixture(context: FixtureContext) {
        val activeExtensions = getAllExtensions(context).reversed()
        activeExtensions.extensionsOfType<CallbackExtension>().forEach {
            it.onAfterFixture(context)
        }
    }

    fun executeAfterDocumentFixture(context: DocumentFixtureContext) {
        val activeExtensions = getAllExtensions(context).reversed()
        activeExtensions.extensionsOfType<CallbackExtension>().forEach {
            it.onAfterDocument(context)
        }
    }

    fun executeAfterGroup(context: GroupContext) {
        val activeExtensions = getAllExtensions(context).reversed()
        activeExtensions.extensionsOfType<CallbackExtension>().forEach {
            it.onAfterGroup(context)
        }
    }

    fun shouldExecute(context: ExtensionContext): Boolean {
        return getAllExtensions(context).extensionsOfType<ExecutionCondition>()
            .map { it.evaluateExecutionCondition(context) }
            .all { it.enabled }
    }

    @Suppress("TooGenericExceptionCaught")
    fun handleTestExecutionException(context: FixtureContext, throwable: Throwable): Throwable? {
        return getAllExtensions(context).extensionsOfType<TestExecutionExceptionHandler>()
            .fold(throwable) { currentThrowable, handler ->
                try {
                    handler.handleTestExecutionException(context, currentThrowable)
                    return null
                } catch (t: Throwable) {
                    t
                }
            }
    }

    fun loadExtensions(context: ExtensionContext) {
        context.extensionStore.extensions = context.extensionClasses.map { instantiateExtension(it) }
    }
}

fun instantiateExtension(extensionClass: KClass<*>): CallbackExtension {
    return extensionClass.castToClass(CallbackExtension::class).createInstance()
}

private val ExtensionContext.extensionStore: Store
    get() = this.getStore("org.livingdoc.jvm.engine.manager.ExtensionManager")

private val ExtensionContext.extensionClasses: List<KClass<*>>
    get() = this.testClass.findAnnotation<org.livingdoc.api.Extensions>()?.value?.toList() ?: emptyList()

private var Store.extensions: List<Extension>
    get() = getListCombineAncestors("extensions").filterIsInstance<Extension>()
    set(value) {
        put("extensions", value)
    }

private inline fun <reified T : Extension> List<Extension>.extensionsOfType(): List<T> {
    return this.filterIsInstance<T>()
}
