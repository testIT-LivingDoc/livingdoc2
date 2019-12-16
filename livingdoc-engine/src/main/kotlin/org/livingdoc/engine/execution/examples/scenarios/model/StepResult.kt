package org.livingdoc.engine.execution.examples.scenarios.model

import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.decisiontables.model.DecisionTableResult
import java.lang.reflect.Method
import java.util.*

data class StepResult private constructor(
    val value: String,
    val status: Status,
    val fixtureMethod: Optional<Method>
) {
    class Builder {
        private lateinit var status: Status
        private lateinit var value: String
        private var fixtureMethod: Method? = null

        /**
         * Sets or overrides the status for the built [StepResult]
         *
         * @param status Can be any [Status] except [Status.Unknown]
         */
        fun withStatus(status: Status): Builder {
            this.status = status
            return this
        }

        /**
         * Sets or overrides the value of a scenario step that the built [DecisionTableResult] refers to
         */
        fun withValue(value: String): Builder {
            this.value = value
            return this
        }

        /**
         * Sets or overrides the [Method] that the built [StepResult] refers to
         */
        fun withFixtureMethod(fixtureMethod: Method): Builder {
            this.fixtureMethod = fixtureMethod
            return this
        }

        /**
         * Build an immutable [StepResult]
         *
         * @returns A new [StepResult] with the data from this builder
         * @throws IllegalStateException If the builder is missing data to build a [StepResult]
         */
        fun build(): StepResult {
            // Check value
            if (!this::value.isInitialized) {
                throw IllegalStateException("Cannot build StepResult with unknown value")
            }

            // Check status
            if (!this::status.isInitialized) {
                throw IllegalStateException("Cannot build StepResult with unknown status")
            }

            return StepResult(this.value, this.status, Optional.ofNullable(this.fixtureMethod))
        }
    }
}
