package org.livingdoc.jvm.api.fixture

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
     * @param testData The TestData which is the input for this test execution
     * @return A TestDataResult for the execution
     */
    fun execute(testData: T): TestDataResult<T>
}
