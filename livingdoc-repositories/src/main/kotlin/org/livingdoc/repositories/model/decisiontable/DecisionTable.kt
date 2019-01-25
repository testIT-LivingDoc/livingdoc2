package org.livingdoc.repositories.model.decisiontable

import org.livingdoc.repositories.model.Example

data class DecisionTable(
    val headers: List<Header>,
    val rows: List<Row>
) : Example
