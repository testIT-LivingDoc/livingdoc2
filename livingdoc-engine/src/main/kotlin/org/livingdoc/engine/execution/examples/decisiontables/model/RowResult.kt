package org.livingdoc.engine.execution.examples.decisiontables.model

import org.livingdoc.engine.execution.Status
import org.livingdoc.repositories.model.decisiontable.Header

data class RowResult(
    val headerToField: Map<Header, FieldResult>,
    var status: Status = Status.Unknown
)
