package org.livingdoc.engine

import org.livingdoc.jvm.extension.FixtureContext
import org.livingdoc.repositories.model.TestData
import org.livingdoc.results.TestDataResult

/**
 * This interface is the basis for all specialized fixture classes.
 * It wraps or represents a fixture and offers an execute function to execute it with some context
 */
interface Fixture<T : TestData> {
    /**
     * Executes the fixture class with the give context
     *
     * @param context The context with all information for an execution of tests
     * @return A TestDataResult for the execution
     */
    fun execute(context: FixtureContext<T>): TestDataResult<T>
}
