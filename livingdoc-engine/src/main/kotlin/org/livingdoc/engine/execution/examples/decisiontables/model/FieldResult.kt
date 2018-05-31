package org.livingdoc.engine.execution.examples.decisiontables.model

import org.livingdoc.engine.execution.Result

data class FieldResult(
    val value: String,
    var result: Result = Result.Unknown
)
