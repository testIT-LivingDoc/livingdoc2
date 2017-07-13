package org.livingdoc.engine.execution.examples.scenarios.model

import org.livingdoc.engine.execution.Result

data class StepResult(
        val value: String,
        var result: Result = Result.Unknown
)