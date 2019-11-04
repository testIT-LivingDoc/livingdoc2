package org.livingdoc.engine.execution

import org.livingdoc.engine.execution.examples.ExampleResult

data class DocumentResult(
    val documentResult: Result = Result.Unknown,
    val results: List<ExampleResult> = emptyList()
)
