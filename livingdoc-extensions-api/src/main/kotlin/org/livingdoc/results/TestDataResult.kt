package org.livingdoc.results

/**
 * Base interface for all results.
 */
interface TestDataResult<out T> {
    /**
     * The [Status] of the [TestDataResult].
     *
     * @see [Status]
     */
    val status: Status
}
