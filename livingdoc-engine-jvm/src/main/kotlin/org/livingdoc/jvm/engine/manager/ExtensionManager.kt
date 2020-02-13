package org.livingdoc.jvm.engine.manager

import org.livingdoc.jvm.engine.castToClass
import org.livingdoc.jvm.api.extension.context.DocumentFixtureContext
import org.livingdoc.jvm.api.extension.context.ExtensionContext
import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.jvm.api.extension.context.GroupContext
import org.livingdoc.jvm.api.extension.context.Store
import org.livingdoc.jvm.api.extension.CallbackExtension
import org.livingdoc.jvm.api.extension.ExecutionCondition
import org.livingdoc.jvm.api.extension.Extension
import org.livingdoc.jvm.api.extension.LifecycleMethodExecutionExceptionHandler
import org.livingdoc.jvm.api.extension.TestExecutionExceptionHandler
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
            .map { handler -> { t: Throwable -> handler.handleTestExecutionException(context, t) } }
            .handle(throwable)
    }

    @Suppress("TooGenericExceptionCaught")
    fun handleBeforeMethodExecutionException(context: ExtensionContext, throwable: Throwable): Throwable? {
        return getAllExtensions(context).extensionsOfType<LifecycleMethodExecutionExceptionHandler>()
            .map { handler -> { t: Throwable -> handler.handleBeforeMethodExecutionException(context, t) } }
            .handle(throwable)
    }

    @Suppress("TooGenericExceptionCaught")
    fun handleAfterMethodExecutionException(context: ExtensionContext, throwable: Throwable): Throwable? {
        return getAllExtensions(context).extensionsOfType<LifecycleMethodExecutionExceptionHandler>()
            .map { handler -> { t: Throwable -> handler.handleAfterMethodExecutionException(context, t) } }
            .handle(throwable)
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

private fun List<(Throwable) -> Unit>.handle(throwable: Throwable): Throwable? {
    return fold(throwable) { currentThrowable, handler ->
        try {
            handler(currentThrowable)
            return null
        } catch (t: Throwable) {
            t
        }
    }
}
