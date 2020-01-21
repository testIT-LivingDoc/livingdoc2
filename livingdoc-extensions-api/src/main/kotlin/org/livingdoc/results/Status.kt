package org.livingdoc.results

sealed class Status {

    /** Nothing is known about the result state. */
    object Unknown : Status()

    /** The fixture was disabled and is ignored */
    data class Disabled(val reason: String = "") : Status()

    /** The fixture will be tested manually */
    object Manual : Status()

    /** Execution was skipped. */
    object Skipped : Status()

    /** Successfully executed. */
    object Executed : Status()

    /** A validation failed with an assertion error. */
    data class Failed(val reason: AssertionError) : Status()

    /** The actual result is reported but not compared to an expected result */
    data class ReportActualResult(val actualResult: String = "") : Status()

    /** An unexpected exception occurred. */
    data class Exception(val exception: Throwable) : Status()
}
