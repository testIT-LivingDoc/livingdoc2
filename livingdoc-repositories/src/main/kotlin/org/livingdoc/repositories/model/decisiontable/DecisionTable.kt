package org.livingdoc.repositories.model.decisiontable

import org.livingdoc.repositories.model.TestData

data class DecisionTable(
    val headers: List<Header>,
    val rows: List<Row>
) : TestData
