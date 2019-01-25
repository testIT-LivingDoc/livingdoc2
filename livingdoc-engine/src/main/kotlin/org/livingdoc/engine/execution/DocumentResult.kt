package org.livingdoc.engine.execution

import org.livingdoc.engine.execution.examples.ExampleResult

data class DocumentResult(
    val results: List<ExampleResult> = emptyList()
)
