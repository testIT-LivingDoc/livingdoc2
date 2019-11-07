package org.livingdoc.engine.execution.examples.scenarios.model

import org.livingdoc.engine.execution.Status

data class StepResult(
    val value: String,
    var status: Status = Status.Unknown
)
