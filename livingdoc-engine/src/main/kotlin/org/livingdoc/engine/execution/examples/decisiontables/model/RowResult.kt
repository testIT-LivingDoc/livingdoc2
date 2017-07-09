package org.livingdoc.engine.execution.examples.decisiontables.model

import org.livingdoc.engine.execution.Result


data class RowResult(
        val headerToField: Map<Header, FieldResult>,
        var result: Result = Result.Unknown
)