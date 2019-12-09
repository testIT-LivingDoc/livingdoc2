package org.livingdoc.engine.execution.examples.scenarios.model

import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.TestDataResult
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.repositories.model.scenario.Scenario

data class ScenarioResult private constructor(
    val steps: List<StepResult>,
    val status: Status,
    val fixture: Fixture<Scenario>?,
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
            when (step.status) {
                is Status.Failed -> {
                    status = Status.Failed(step.status.reason)
                }
                is Status.Exception -> {
                    status = Status.Exception(step.status.exception)
                }
            }
            return this
        }

        fun withFixture(fixture: Fixture<Scenario>): Builder {
            this.fixture = fixture
            return this
        }

        fun withScenario(scenario: Scenario): Builder {
            this.scenario = scenario
            return this
        }

        fun build(): ScenarioResult {
            when {
                this.fixture == null -> {
                    // TODO Can't add this check until execution is part of fixture class
                    // throw IllegalArgumentException("Cant't build ScenarioResult without a fixture")
                }
                this.scenario == null -> {
                    throw IllegalArgumentException("Cannot build ScenarioResult without a scenario")
                }
            }

            when (this.status) {
                is Status.Unknown -> {
                    throw IllegalArgumentException("Cannot build ScenarioResult with unknown status")
                }
                is Status.Manual, is Status.Disabled -> {
                    this.steps = scenario!!.steps.map {
                        StepResult.Builder()
                            .withStatus(this.status)
                            .withValue(it.value)
                            .build()
                    }.toMutableList()
                }
            }

            // Do all scenario steps have a valid result?
            scenario!!.steps.forEach {
                val step = it
                if (steps.filter {
                        it.value == step.value && it.status != Status.Unknown
                    }.isEmpty()) {
                    throw java.lang.IllegalArgumentException("Not all scenario steps are contained in the result")
                }
            }

            return ScenarioResult(this.steps, this.status, this.fixture, this.scenario!!)
        }
    }
}
