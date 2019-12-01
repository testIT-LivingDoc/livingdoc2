package org.livingdoc.engine.fixtures

import org.livingdoc.engine.execution.examples.TestDataResult
import org.livingdoc.repositories.model.TestData

interface Fixture {
    /**
     * Executes the fixture class with the give testData
     *
     * @param testData A test data instance that can be mapped to the fixture
     * @return A TestDataResult for the execution
     */
    fun execute(testData: TestData): TestDataResult
}
