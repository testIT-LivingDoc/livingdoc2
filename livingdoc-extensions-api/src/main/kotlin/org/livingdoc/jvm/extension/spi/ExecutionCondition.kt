package org.livingdoc.jvm.extension.spi

import org.livingdoc.jvm.extension.ExtensionContext

interface ExecutionCondition : Extension {
    fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult
}
