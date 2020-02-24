package org.livingdoc.jvm.api.extension

import org.livingdoc.jvm.api.extension.context.ExtensionContext

interface ExecutionCondition : Extension {
    fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult
}
