package org.livingdoc.jvm.engine.extension.defaults

import org.livingdoc.api.disabled.Disabled
import org.livingdoc.jvm.api.extension.context.ExtensionContext
import org.livingdoc.jvm.api.extension.ConditionEvaluationResult
import org.livingdoc.jvm.api.extension.ExecutionCondition
import kotlin.reflect.full.findAnnotation

/**
 * ExecutionCondition that supports the {@code @Disabled} annotation.
 */
class DisabledCondition : ExecutionCondition {
    override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult {
        val disabled = context.testClass.findAnnotation<Disabled>()
        return if (disabled == null) {
            ConditionEvaluationResult.enabled("@Disabled is not present")
        } else {
            ConditionEvaluationResult.disabled(disabled.value)
        }
    }
}
