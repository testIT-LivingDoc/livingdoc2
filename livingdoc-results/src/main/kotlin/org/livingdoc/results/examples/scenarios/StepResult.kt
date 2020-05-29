package org.livingdoc.results.examples.scenarios

import org.livingdoc.results.Status
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import java.lang.reflect.Method

data class StepResult private constructor(
    val value: String,
    val status: Status,
    val fixtureMethod: Method?
) {
    /**
     * A not threadsafe builder class for [StepResult] objects
     */
    class Builder {
        private lateinit var status: Status
        private lateinit var value: String
        private var fixtureMethod: Method? = null

        private var finalized = false

        private fun checkFinalized() {
            if (this.finalized)
                throw IllegalStateException(
                    "This StepResult.Builder has already been finalized and can't be altered anymore."
                )
        }

        /**
         * Sets or overrides the status for the built [StepResult]
         *
         * @param status Can be any [Status] except [Status.Unknown]
         */
        fun withStatus(status: Status): Builder {
            checkFinalized()
            this.status = status
            return this
        }

        /**
         * Sets or overrides the value of a scenario step that the built [DecisionTableResult] refers to
         */
        fun withValue(value: String): Builder {
            checkFinalized()
            this.value = value
            return this
        }

        /**
         * Sets or overrides the [Method] that the built [StepResult] refers to
         */
        fun withFixtureMethod(fixtureMethod: Method): Builder {
            checkFinalized()
            this.fixtureMethod = fixtureMethod
            return this
        }

        /**
         * Build an immutable [StepResult]
         *
         * WARNING: The builder will be finalized and can not be altered after calling this function
         *
         * @returns A new [StepResult] with the data from this builder
         * @throws IllegalStateException If the builder is missing data to build a [StepResult]
         */
        fun build(): StepResult {
            // Finalize this builder. No further changes are allowed
            this.finalized = true

            // Check value
            if (!this::value.isInitialized) {
                throw IllegalStateException("Cannot build StepResult with unknown value")
            }
            val value = this.value

            // Check status
            if (!this::status.isInitialized) {
                throw IllegalStateException("Cannot build StepResult with unknown status")
            }
            val status = this.status

            // Check fixture method
            val fixtureMethod = this.fixtureMethod

            return StepResult(value, status, fixtureMethod)
        }
    }
}
