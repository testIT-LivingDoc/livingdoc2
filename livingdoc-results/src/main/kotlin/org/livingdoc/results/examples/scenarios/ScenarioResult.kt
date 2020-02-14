package org.livingdoc.results.examples.scenarios

import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.results.Status
import org.livingdoc.results.TestDataResult

data class ScenarioResult private constructor(
    val steps: List<StepResult>,
    override val status: Status,
    val fixtureSource: Class<*>?,
    val scenario: Scenario
) : TestDataResult<Scenario> {
    /**
     * A not threadsafe builder class for [ScenarioResult] objects
     */
    class Builder {
        private lateinit var status: Status
        private var steps = mutableListOf<StepResult>()
        private var fixtureSource: Class<*>? = null
        private var scenario: Scenario? = null

        private var finalized = false

        private fun checkFinalized() {
            if (this.finalized)
                throw IllegalStateException(
                    "This ScenarioResult.Builder has already been finalized and can't be altered anymore."
                )
        }

        /**
         * Sets or overrides the status for the built [ScenarioResult]
         *
         * @param status Can be any [Status] except [Status.Unknown]
         */
        fun withStatus(status: Status): Builder {
            checkFinalized()
            this.status = status
            return this
        }

        /**
         * Sets the [StepResult] for a step in the given [Scenario]
         *
         * @param step A successfully built [StepResult]
         */
        fun withStep(step: StepResult): Builder {
            checkFinalized()

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
         * Sets or overrides the [fixtureSource] that defines the implementation of Fixture.
         * This value is optional.
         */
        fun withFixtureSource(fixtureSource: Class<*>): Builder {
            checkFinalized()
            this.fixtureSource = fixtureSource
            return this
        }

        /**
         * Sets or overrides the [Scenario] that the built [ScenarioResult] refers to
         */
        fun withScenario(scenario: Scenario): Builder {
            checkFinalized()
            this.scenario = scenario
            return this
        }

        /**
         * Marks all steps that have no result yet with [Status.Skipped]
         */
        fun withUnassignedSkipped(): Builder {
            checkFinalized()
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
         * WARNING: The builder will be finalized and can not be altered after calling this function
         *
         * @returns A new [ScenarioResult] with the data from this builder
         * @throws IllegalStateException If the builder is missing data to build a [ScenarioResult]
         */
        fun build(): ScenarioResult {
            // Finalize this builder. No further changes are allowed
            this.finalized = true

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
            return ScenarioResult(
                steps,
                status,
                fixtureSource,
                scenario
            )
        }
    }
}
