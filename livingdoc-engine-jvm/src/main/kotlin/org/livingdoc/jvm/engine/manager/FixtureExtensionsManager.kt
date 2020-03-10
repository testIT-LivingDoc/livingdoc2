package org.livingdoc.jvm.engine.manager

import org.livingdoc.jvm.api.extension.ConditionEvaluationResult
import org.livingdoc.jvm.api.fixture.FixtureManager
import org.livingdoc.jvm.engine.EngineContext

/**
 * Wrapper for the ExtensionManager as FixtureExtensionsInterface. This is used to give Fixtures access to engine
 * functionality.
 */
internal class FixtureExtensionsManager(
    private val extensionManager: ExtensionManager,
    private val context: EngineContext
) : FixtureManager {
    override fun shouldExecute(): ConditionEvaluationResult {
        return extensionManager.shouldExecute(context)
    }

    override fun onBeforeFixture() {
        extensionManager.executeBeforeFixture(context)
    }

    override fun handleBeforeMethodExecutionException(throwable: Throwable): Throwable? {
        return extensionManager.handleBeforeMethodExecutionException(context, throwable)
    }

    override fun handleTestExecutionException(throwable: Throwable): Throwable? {
        return extensionManager.handleTestExecutionException(context, throwable)
    }

    override fun handleAfterMethodExecutionException(throwable: Throwable): Throwable? {
        return extensionManager.handleAfterMethodExecutionException(context, throwable)
    }

    override fun onAfterFixture() {
        extensionManager.executeAfterFixture(context)
    }
}
