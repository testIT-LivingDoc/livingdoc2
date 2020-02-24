package org.livingdoc.results

/**
 * Base interface for all results of Fixtures.
 */
interface TestDataResult<out T> : Result {
    /**
     * The [Status] of the [TestDataResult].
     *
     * @see [Status]
     */
    override val status: Status
}
