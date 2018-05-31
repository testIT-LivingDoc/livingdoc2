package org.livingdoc.repositories.model.decisiontable

data class DecisionTable(
    val headers: List<Header>,
    val rows: List<Row>
)
