package org.livingdoc.jvm.engine.manager

import org.livingdoc.jvm.api.extension.CallbackExtension
import org.livingdoc.jvm.api.extension.ConditionEvaluationResult
import org.livingdoc.jvm.api.extension.ExecutionCondition
import org.livingdoc.jvm.api.extension.Extension
import org.livingdoc.jvm.api.extension.LifecycleMethodExecutionExceptionHandler
import org.livingdoc.jvm.api.extension.TestExecutionExceptionHandler
import org.livingdoc.jvm.api.extension.context.DocumentFixtureContext
import org.livingdoc.jvm.api.extension.context.ExtensionContext
import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.jvm.api.extension.context.GroupContext
import org.livingdoc.jvm.engine.EngineContext
import kotlin.reflect.KClass

internal class ExtensionManager {

    fun executeBeforeGroup(context: EngineContext) {
        executeAllBeforeCallbacks(context, CallbackExtension::class) { callback, extensionsContext ->
            callback.onBeforeGroup(extensionsContext as GroupContext)
        }
    }

    fun executeBeforeDocumentFixture(context: EngineContext) {
        executeAllBeforeCallbacks(context, CallbackExtension::class) { callback, extensionsContext ->
            callback.onBeforeDocument(extensionsContext as DocumentFixtureContext)
        }
    }

    fun executeBeforeFixture(context: EngineContext) {
        executeAllBeforeCallbacks(context, CallbackExtension::class) { callback, extensionsContext ->
            callback.onBeforeFixture(extensionsContext as FixtureContext)
        }
    }

    fun executeAfterFixture(context: EngineContext) {
        executeAllAfterCallbacks(context, CallbackExtension::class) { callback, extensionsContext ->
            callback.onAfterFixture(extensionsContext as FixtureContext)
        }
    }

    fun executeAfterDocumentFixture(context: EngineContext) {
        executeAllAfterCallbacks(context, CallbackExtension::class) { callback, extensionsContext ->
            callback.onAfterDocument(extensionsContext as DocumentFixtureContext)
        }
    }

    fun executeAfterGroup(context: EngineContext) {
        executeAllAfterCallbacks(context, CallbackExtension::class) { callback, extensionsContext ->
            callback.onAfterGroup(extensionsContext as GroupContext)
        }
    }

    /**
     * call the Extensions in order they was registered and catch all exceptions thrown
     */
    private fun <T : Extension> executeAllBeforeCallbacks(
        context: EngineContext,
        type: KClass<T>,
        callbackInvoker: (callback: T, context: ExtensionContext) -> Unit
    ) {
        val activeExtensions = context.extensionRegistry
        val extensionContext = context.extensionContext
        val throwableCollector = context.throwableCollector
        activeExtensions.getExtensions(type).forEach {
            throwableCollector.execute { callbackInvoker.invoke(it, extensionContext) }
        }
    }

    /**
     * call the Extensions in reverse order and catch all exceptions thrown
     */
    private fun <T : Extension> executeAllAfterCallbacks(
        context: EngineContext,
        type: KClass<T>,
        callbackInvoker: (callback: T, context: ExtensionContext) -> Unit
    ) {
        val activeExtensions = context.extensionRegistry
        val extensionContext = context.extensionContext
        val throwableCollector = context.throwableCollector
        activeExtensions.getExtensionsReverse(type).forEach {
            throwableCollector.execute { callbackInvoker.invoke(it, extensionContext) }
        }
    }

    fun shouldExecute(context: EngineContext): ConditionEvaluationResult {
        return context.extensionRegistry.getExtensions(ExecutionCondition::class)
            .map { it.evaluateExecutionCondition(context.extensionContext) } // TODO handle exception form extensions
            .find { it.disabled } ?: ConditionEvaluationResult.enabled("No 'disabled' conditions encountered")
    }

    fun handleTestExecutionException(context: EngineContext, throwable: Throwable): Throwable? {
        return context.extensionRegistry.getExtensions(TestExecutionExceptionHandler::class)
            .map { handler -> { t: Throwable -> handler.handleTestExecutionException(context.extensionContext, t) } }
            .handle(throwable)
    }

    fun handleBeforeMethodExecutionException(context: EngineContext, throwable: Throwable): Throwable? {
        return context.extensionRegistry.getExtensions(LifecycleMethodExecutionExceptionHandler::class)
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
        return context.extensionRegistry.getExtensions(LifecycleMethodExecutionExceptionHandler::class)
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
