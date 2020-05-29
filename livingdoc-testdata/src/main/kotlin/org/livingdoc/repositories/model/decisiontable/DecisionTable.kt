package org.livingdoc.repositories.model.decisiontable

import org.livingdoc.repositories.Document
import org.livingdoc.repositories.model.TestData
import org.livingdoc.repositories.model.TestDataDescription

/**
 * DecisionTable contains all information that pertains to a decision table contained in a [Document]
 */
data class DecisionTable(
    val headers: List<Header>,
    val rows: List<Row>,
    override val description: TestDataDescription = TestDataDescription(null, false, "")
) : TestData
