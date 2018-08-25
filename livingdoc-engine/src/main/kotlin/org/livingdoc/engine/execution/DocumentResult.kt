package org.livingdoc.engine.execution

import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult

data class DocumentResult(
        val decisionTableResults: List<DecisionTableResult> = emptyList(),
        val scenarioResults: List<ScenarioResult> = emptyList()
)
