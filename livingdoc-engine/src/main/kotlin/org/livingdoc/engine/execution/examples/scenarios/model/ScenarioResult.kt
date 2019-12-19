package org.livingdoc.engine.execution.examples.scenarios.model

import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.TestDataResult
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.repositories.model.scenario.Scenario
import java.util.*

data class ScenarioResult private constructor(
    val steps: List<StepResult>,
    val status: Status,
    val fixture: Fixture<Scenario>?,
    val fixtureSource: Optional<Class<*>>,
    val scenario: Scenario
) : TestDataResult {
    class Builder {
        private lateinit var status: Status
        private var steps = mutableListOf<StepResult>()
        private var fixture: Fixture<Scenario>? = null
        private var fixtureSource = Optional.empty<Class<*>>()
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
         * Sets or overrides the [fixtureSource] that defines the implementation of [Fixture].
         * This value is optional.
         */
        fun withFixtureSource(fixtureSource: Class<*>): Builder {
            this.fixtureSource = Optional.of(fixtureSource)
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
            if (!this::status.isInitialized) {
                throw IllegalStateException("Cannot build ScenarioResult with unknown status")
            }
            val status = this.status
            val steps = if (status is Status.Manual || status is Status.Disabled)
                scenario.steps.map {
                    StepResult.Builder()
                        .withStatus(this.status)
                        .withValue(it.value)
                        .build()
                }.toMutableList()
            else
                this.steps

            // Do all scenario steps have a valid result?
            if (steps.size != scenario.steps.size) {
                throw IllegalStateException(
                    "Cannot build ScenarioResult. The number of step results (${steps.size})" +
                            " does not match the expected number (${scenario.steps.size})"
                )
            }
            scenario.steps.forEach { step ->
                if (steps.none {
                        it.value == step.value && it.status != Status.Unknown
                    }) {
                    throw IllegalStateException("Not all scenario steps are contained in the result")
                }
            }

            // Build result
            return ScenarioResult(steps, status, fixture, fixtureSource, scenario)
        }
    }
}
