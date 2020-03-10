package org.livingdoc.jvm.api.extension

/**
 * The result of evaluating an [ExecutionCondition].
 *
 * @property enabled Whether the test should be enabled or disabled.
 * @property reason The reason why the test should be enabled or disabled, if available.
 */
data class ConditionEvaluationResult(
    val enabled: Boolean,
    val reason: String?
) {
    val disabled get() = !enabled

    companion object {
        /**
         * Create a disabled ConditionEvaluationResult with an optional [reason] why the test should be disabled
         */
        fun disabled(reason: String? = null) =
            ConditionEvaluationResult(false, reason)

        /**
         * Create a enabled ConditionEvaluationResult with an optional [reason] why the test should be enabled
         */
        fun enabled(reason: String? = null) =
            ConditionEvaluationResult(true, reason)
    }
}
