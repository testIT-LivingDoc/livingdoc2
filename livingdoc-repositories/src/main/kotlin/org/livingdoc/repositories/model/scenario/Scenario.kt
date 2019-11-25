package org.livingdoc.repositories.model.scenario

import org.livingdoc.repositories.model.TestData
import org.livingdoc.repositories.model.TestDataDescription

data class Scenario(
    val steps: List<Step>,
    override val description: TestDataDescription = TestDataDescription(null, false)
) : TestData
