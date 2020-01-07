package org.livingdoc.engine.fixtures

import org.livingdoc.repositories.model.TestData
import org.livingdoc.results.TestDataResult

/**
 * This interface is the basis for all specialized fixture classes.
 * It wraps or represents a fixture and offers an execute function to execute it with some test data
 */
interface Fixture<T : TestData> {
    /**
     * Executes the fixture class with the give testData
     *
     * @param testData A test data instance that can be mapped to the fixture
     * @return A TestDataResult for the execution
     */
    fun execute(testData: T): TestDataResult<T>
}
