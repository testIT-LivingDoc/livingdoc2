package org.livingdoc.jvm.engine.extension.defaults

import org.livingdoc.api.documents.FailFast
import org.livingdoc.jvm.api.extension.ConditionEvaluationResult
import org.livingdoc.jvm.api.extension.ExecutionCondition
import org.livingdoc.jvm.api.extension.TestExecutionExceptionHandler
import org.livingdoc.jvm.api.extension.context.ExtensionContext
import org.livingdoc.jvm.api.extension.context.Store
import kotlin.reflect.KClass

class FailFast : TestExecutionExceptionHandler, ExecutionCondition {

    override fun handleTestExecutionException(context: ExtensionContext, throwable: Throwable) {
        val failFastExceptions = getFailFastExceptionTypes(context)
        if (failFastExceptions.any { it.isInstance(throwable) }) {
            getStore(context).put("fail-fast-active", true)
        }
        throw throwable
    }

    override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult {
        return if (getStore(context).get("fail-fast-active") == true) {
            ConditionEvaluationResult.disabled("Fail fast activated")
        } else {
            ConditionEvaluationResult.enabled(null)
        }
    }

    private fun getStore(context: ExtensionContext): Store {
        return context.getStore(org.livingdoc.jvm.engine.extension.defaults.FailFast::class.qualifiedName!!)
    }

    private fun getFailFastExceptionTypes(context: ExtensionContext): List<KClass<out Throwable>> {
        val exceptions =
            context.testClass.annotations.filterIsInstance<FailFast>().flatMap { it.onExceptionTypes.asIterable() }
        val parent = context.parent
        return if (parent == null) exceptions else getFailFastExceptionTypes(parent) + exceptions
    }
}
