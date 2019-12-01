package org.livingdoc.engine.fixtures

import org.livingdoc.engine.execution.examples.TestDataResult
import org.livingdoc.repositories.model.TestData

interface FixtureWrapper {
    fun execute(testData: TestData): TestDataResult
}
