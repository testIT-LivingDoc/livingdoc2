package org.livingdoc.engine.execution.examples.decisiontables.model

import org.livingdoc.engine.execution.Status
import org.livingdoc.repositories.model.decisiontable.DecisionTable

data class FieldResult private constructor(
    val value: String,
    val status: Status
) {
    class Builder {
        private var value: String? = null
        private var status: Status = Status.Unknown

        /**
         * Sets or overrides the value of a [DecisionTable] row that the built [FieldResult] refers to
         */
        fun withValue(value: String): Builder {
            this.value = value
            return this
        }

        /**
         * Sets or overrides the status for the built [FieldResult]
         *
         * @param status Can be any [Status] except [Status.Unknown]
         */
        fun withStatus(status: Status): Builder {
            this.status = status
            return this
        }

        /**
         * Build an immutable [FieldResult]
         *
         * @returns A new [FieldResult] with the data from this builder
         * @throws IllegalStateException If the builder is missing data to build a [FieldResult]
         */
        fun build(): FieldResult {
            if (this.status == Status.Unknown)
                throw IllegalStateException("Cannot build FieldResult with unknown status")

            val value = this.value ?: throw IllegalArgumentException("Cannot build FieldResult without a value")

            return FieldResult(value, this.status)
        }

    }
}
