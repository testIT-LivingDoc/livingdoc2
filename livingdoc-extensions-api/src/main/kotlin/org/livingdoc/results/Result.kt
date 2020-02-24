package org.livingdoc.results

/**
 * Base interface for all results.
 */
interface Result {
    /**
     * The [Status] of the Result.
     *
     * @see [Status]
     */
    val status: Status
}
