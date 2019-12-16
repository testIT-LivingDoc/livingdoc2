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
        private var steps = mutableListOf<StepResult>()
        private var fixture: Fixture<Scenario>? = null
        private var scenario: Scenario? = null

        /**
         * Sets or overrides the status for the built [ScenarioResult]
         *
         * @param status Can be any [Status] except [Status.Unknown]
         */
        fun withStatus(status: Status): Builder {
            this.status = status
            return this
        }

        /**
         * Sets the [StepResult] for a step in the given [Scenario]
         *
         * @param step A sucessfully built [StepResult]
         */
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

        /**
         * Sets or overrides the [Fixture] that the built [ScenarioResult] refers to
         */
        fun withFixture(fixture: Fixture<Scenario>): Builder {
            this.fixture = fixture
            return this
        }

        /**
         * Sets or overrides the [Scenario] that the built [ScenarioResult] refers to
         */
        fun withScenario(scenario: Scenario): Builder {
            this.scenario = scenario
            return this
        }

        /**
         * Marks all steps that have no result yet with [Status.Skipped]
         */
        fun withUnassignedSkipped(): Builder {
            this.scenario!!.steps.forEach {
                withStep(
                    StepResult.Builder()
                        .withValue(it.value)
                        .withStatus(Status.Skipped)
                        .build()
                )
            }
            return this
        }

        /**
         * Build an immutable [ScenarioResult]
         *
         * @returns A new [ScenarioResult] with the data from this builder
         * @throws IllegalStateException If the builder is missing data to build a [ScenarioResult]
         */
        fun build(): ScenarioResult {
            // TODO Can't add this check until execution is part of fixture class
            val fixture = this.fixture
            // ?: throw IllegalStateException("Cant't build ScenarioResult without a fixture")

            val scenario =
                this.scenario ?: throw IllegalStateException("Cannot build ScenarioResult without a scenario")

            // Check status
            when (this.status) {
                is Status.Unknown -> {
                    throw IllegalStateException("Cannot build ScenarioResult with unknown status")
                }
                is Status.Manual, is Status.Disabled -> {
                    this.steps = scenario.steps.map {
                        StepResult.Builder()
                            .withStatus(this.status)
                            .withValue(it.value)
                            .build()
                    }.toMutableList()
                }
            }

            // Do all scenario steps have a valid result?
            scenario.steps.forEach {
                val step = it
                if (steps.filter {
                        it.value == step.value && it.status != Status.Unknown
                    }.isEmpty()) {
                    throw IllegalStateException("Not all scenario steps are contained in the result")
                }
            }

            // Build result
            return ScenarioResult(this.steps, this.status, fixture, scenario)
        }
    }
}
