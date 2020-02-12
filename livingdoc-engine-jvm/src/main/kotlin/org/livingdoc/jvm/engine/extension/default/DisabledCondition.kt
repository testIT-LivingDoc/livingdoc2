package org.livingdoc.jvm.engine.extension.default

import org.livingdoc.api.disabled.Disabled
import org.livingdoc.jvm.extension.ExtensionContext
import org.livingdoc.jvm.extension.spi.ConditionEvaluationResult
import org.livingdoc.jvm.extension.spi.ExecutionCondition
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