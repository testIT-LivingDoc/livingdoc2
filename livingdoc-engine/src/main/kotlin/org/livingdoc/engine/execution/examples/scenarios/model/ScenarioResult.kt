package org.livingdoc.engine.execution.examples.scenarios.model

import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.TestDataResult
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.repositories.model.scenario.Scenario

data class ScenarioResult private constructor(
    val steps: List<StepResult>,
    val status: Status,
    val fixture: Fixture<Scenario>,
    val scenario: Scenario
) : TestDataResult {
    class Builder {
        private var status: Status = Status.Unknown
        private var steps: MutableList<StepResult> = ArrayList()
        private var fixture: Fixture<Scenario>? = null
        private var scenario: Scenario? = null

        fun withStatus(status: Status): Builder {
            this.status = status
            return this
        }

        fun withStep(step: StepResult): Builder {
            steps.add(step)
            return this
        }

        fun ofFixture(fixture: Fixture<Scenario>): Builder {
            this.fixture = fixture
            return this
        }

        fun ofScenario(scenario: Scenario): Builder {
            this.scenario = scenario
            return this
        }

        fun build(): ScenarioResult {
            // TODO validate result
            return when {
                this.fixture == null -> {
                    throw IllegalArgumentException("Cant't build ScenarioResult without a fixture")
                }
                this.scenario == null -> {
                    throw IllegalArgumentException("Cant't build ScenarioResult without a scenario")
                }
                else -> {
                    ScenarioResult(this.steps, this.status, this.fixture!!, this.scenario!!)
                }
            }
        }
    }
}
