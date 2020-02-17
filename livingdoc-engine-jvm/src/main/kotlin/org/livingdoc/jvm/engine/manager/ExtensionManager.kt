package org.livingdoc.jvm.engine.manager

import org.livingdoc.jvm.api.extension.CallbackExtension
import org.livingdoc.jvm.api.extension.ExecutionCondition
import org.livingdoc.jvm.api.extension.Extension
import org.livingdoc.jvm.api.extension.LifecycleMethodExecutionExceptionHandler
import org.livingdoc.jvm.api.extension.TestExecutionExceptionHandler
import org.livingdoc.jvm.api.extension.context.DocumentFixtureContext
import org.livingdoc.jvm.api.extension.context.ExtensionContext
import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.jvm.api.extension.context.GroupContext
import org.livingdoc.jvm.api.extension.context.Store
import org.livingdoc.jvm.engine.EngineContext
import org.livingdoc.jvm.engine.castToClass
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

internal class ExtensionManager {

    private val defaultExtensions = ServiceLoader.load(Extension::class.java).iterator().asSequence().toList()

    /**
     * Get all Extensions form the context and the default Extensions
     */
    private fun getAllExtensions(context: EngineContext): List<Extension> =
        defaultExtensions + context.allExtensions

    fun executeBeforeGroup(context: EngineContext) {
        executeAllBeforeCallbacks<CallbackExtension>(context) { callback, extensionsContext ->
            callback.onBeforeGroup(extensionsContext as GroupContext)
        }
    }

    fun executeBeforeDocumentFixture(context: EngineContext) {
        executeAllBeforeCallbacks<CallbackExtension>(context) { callback, extensionsContext ->
            callback.onBeforeDocument(extensionsContext as DocumentFixtureContext)
        }
    }

    fun executeBeforeFixture(context: EngineContext) {
        executeAllBeforeCallbacks<CallbackExtension>(context) { callback, extensionsContext ->
            callback.onBeforeFixture(extensionsContext as FixtureContext)
        }
    }

    fun executeAfterFixture(context: EngineContext) {
        executeAllAfterCallbacks<CallbackExtension>(context) { callback, extensionsContext ->
            callback.onAfterFixture(extensionsContext as FixtureContext)
        }
    }

    fun executeAfterDocumentFixture(context: EngineContext) {
        executeAllAfterCallbacks<CallbackExtension>(context) { callback, extensionsContext ->
            callback.onAfterDocument(extensionsContext as DocumentFixtureContext)
        }
    }

    fun executeAfterGroup(context: EngineContext) {
        executeAllAfterCallbacks<CallbackExtension>(context) { callback, extensionsContext ->
            callback.onAfterGroup(extensionsContext as GroupContext)
        }
    }

    /**
     * call the Extensions in order they was registered and catch all exceptions thrown
     */
    private inline fun <reified T : Extension> executeAllBeforeCallbacks(
        context: EngineContext,
        crossinline callbackInvoker: (callback: T, context: ExtensionContext) -> Unit
    ) {
        val activeExtensions = getAllExtensions(context)
        val extensionContext = context.extensionContext
        val throwableCollector = context.throwableCollector
        activeExtensions.extensionsOfType<T>().forEach {
            throwableCollector.execute { callbackInvoker.invoke(it, extensionContext) }
        }
    }

    /**
     * call the Extensions in reverse order and catch all exceptions thrown
     */
    private inline fun <reified T : Extension> executeAllAfterCallbacks(
        context: EngineContext,
        crossinline callbackInvoker: (callback: T, context: ExtensionContext) -> Unit
    ) {
        val activeExtensions = getAllExtensions(context).reversed()
        val extensionContext = context.extensionContext
        val throwableCollector = context.throwableCollector
        activeExtensions.extensionsOfType<T>().forEach {
            throwableCollector.execute { callbackInvoker.invoke(it, extensionContext) }
        }
    }

    fun shouldExecute(context: EngineContext): Boolean {
        return getAllExtensions(context).extensionsOfType<ExecutionCondition>()
            .map { it.evaluateExecutionCondition(context.extensionContext) } // TODO handle exception form extensions
            .all { it.enabled }
    }

    fun handleTestExecutionException(context: EngineContext, throwable: Throwable): Throwable? {
        return getAllExtensions(context).extensionsOfType<TestExecutionExceptionHandler>()
            .map { handler -> { t: Throwable -> handler.handleTestExecutionException(context.extensionContext, t) } }
            .handle(throwable)
    }

    fun handleBeforeMethodExecutionException(context: EngineContext, throwable: Throwable): Throwable? {
        return getAllExtensions(context).extensionsOfType<LifecycleMethodExecutionExceptionHandler>()
            .map { handler ->
                { t: Throwable ->
                    handler.handleBeforeMethodExecutionException(
                        context.extensionContext,
                        t
                    )
                }
            }
            .handle(throwable)
    }

    fun handleAfterMethodExecutionException(context: EngineContext, throwable: Throwable): Throwable? {
        return getAllExtensions(context).extensionsOfType<LifecycleMethodExecutionExceptionHandler>()
            .map { handler ->
                { t: Throwable ->
                    handler.handleAfterMethodExecutionException(
                        context.extensionContext,
                        t
                    )
                }
            }
            .handle(throwable)
    }

    fun loadExtensions(context: EngineContext) {
        context.extensions = context.extensionContext.extensionClasses.map { instantiateExtension(it) }
    }
}

fun instantiateExtension(extensionClass: KClass<*>): CallbackExtension {
    return extensionClass.castToClass(CallbackExtension::class).createInstance()
}

private val ExtensionContext.extensionStore: Store
    get() = this.getStore("org.livingdoc.jvm.engine.manager.ExtensionManager")

private val ExtensionContext.extensionClasses: List<KClass<*>>
    get() = this.testClass.findAnnotation<org.livingdoc.api.Extensions>()?.value?.toList() ?: emptyList()

private val EngineContext.allExtensions: List<Extension>
    get() = parent?.allExtensions.orEmpty() + extensions

private inline fun <reified T : Extension> List<Extension>.extensionsOfType(): List<T> {
    return this.filterIsInstance<T>()
}

@Suppress("TooGenericExceptionCaught")
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
