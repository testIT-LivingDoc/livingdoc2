package org.livingdoc.engine.execution.examples.decisiontables.model

import org.livingdoc.engine.execution.Status

data class FieldResult(
    val value: String,
    var status: Status = Status.Unknown
)
