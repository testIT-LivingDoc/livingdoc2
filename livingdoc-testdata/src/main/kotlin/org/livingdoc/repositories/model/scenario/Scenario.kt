package org.livingdoc.repositories.model.scenario

import org.livingdoc.repositories.Document
import org.livingdoc.repositories.model.TestData
import org.livingdoc.repositories.model.TestDataDescription

/**
 * A Scenario represents a scenario contained in a [Document].
 *
 * It consists of a list of [Steps][Step] and has an optional description.
 *
 * @see Document
 */
data class Scenario(
    val steps: List<Step>,
    override val description: TestDataDescription = TestDataDescription(null, false, "")
) : TestData
