package org.livingdoc.jvm.extension.spi

import org.livingdoc.jvm.extension.Context

interface ExecutionCondition : Extension {
    fun evaluateExecutionCondition(context: Context): ConditionEvaluationResult
}
