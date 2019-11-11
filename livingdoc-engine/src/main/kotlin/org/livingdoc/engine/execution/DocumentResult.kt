package org.livingdoc.engine.execution

import org.livingdoc.engine.execution.examples.Result

data class DocumentResult(
    val documentStatus: Status = Status.Unknown,
    val results: List<Result> = emptyList()
)
