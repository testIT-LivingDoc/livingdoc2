package org.livingdoc.engine.execution

sealed class Result {

    /** Nothing is known about the result state. */
    object Unknown : Result()

    /** Execution was skipped. */
    object Skipped : Result()

    /** Successfully executed. */
    object Executed : Result()

    /** A validation failed with an assertion error. */
    data class Failed(val reason: AssertionError) : Result()

    /** An unexpected exception occurred. */
    data class Exception(val exception: Throwable) : Result()

}
