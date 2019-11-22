package org.livingdoc.repositories.model.decisiontable

import org.livingdoc.repositories.model.TestData
import org.livingdoc.repositories.model.TestDataDescription

data class DecisionTable(
    val headers: List<Header>,
    val rows: List<Row>,
    override val description: TestDataDescription = TestDataDescription(null, false)
) : TestData
