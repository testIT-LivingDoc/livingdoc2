package org.livingdoc.jvm.api.fixture

import org.livingdoc.jvm.api.extension.ConditionEvaluationResult

/**
 * This interface should be used by Fixtures to interact with the livingdoc engine.
 *
 * TODO rename this interface
 */
interface FixtureExtensionsInterface {

    fun shouldExecute(): ConditionEvaluationResult

    fun onBeforeFixture()

    fun handleBeforeMethodExecutionException(throwable: Throwable): Throwable?

    fun handleTestExecutionException(throwable: Throwable): Throwable?

    fun handleAfterMethodExecutionException(throwable: Throwable): Throwable?

    fun onAfterFixture()
}
