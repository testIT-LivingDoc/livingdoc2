package org.livingdoc.repositories.model.decisiontable

/**
 * A Row consists of [Fields][Field] for all [Headers][Header] of a [DecisionTable]
 */
data class Row(val headerToField: Map<Header, Field>)
