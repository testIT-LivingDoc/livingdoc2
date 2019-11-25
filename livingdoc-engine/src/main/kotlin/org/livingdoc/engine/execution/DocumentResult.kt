package org.livingdoc.engine.execution

import org.livingdoc.engine.execution.examples.TestDataResult

data class DocumentResult(
    val documentStatus: Status = Status.Unknown,
    val results: List<TestDataResult> = emptyList()
)
