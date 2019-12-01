package org.livingdoc.engine.fixtures

import org.livingdoc.engine.execution.examples.TestDataResult
import org.livingdoc.repositories.model.TestData

interface Fixture {
    fun execute(testData: TestData): TestDataResult
}
