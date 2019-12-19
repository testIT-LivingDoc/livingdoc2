package org.livingdoc.engine.execution.examples.decisiontables.model

import org.livingdoc.engine.execution.Status
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import java.lang.reflect.Method
import java.util.*

data class FieldResult private constructor(
    val value: String,
    val status: Status,
    val method: Optional<Method>
) {
    class Builder {
        private lateinit var value: String
        private lateinit var status: Status
        private var method = Optional.empty<Method>()

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
         * Sets or overrides the [check method][method] for the built [FieldResult]
         */
        fun withCheckMethod(method: Method): Builder {
            this.method = Optional.of(method)
            return this
        }

        /**
         * Build an immutable [FieldResult]
         *
         * @returns A new [FieldResult] with the data from this builder
         * @throws IllegalStateException If the builder is missing data to build a [FieldResult]
         */
        fun build(): FieldResult {
            if (!this::status.isInitialized)
                throw IllegalStateException("Cannot build FieldResult with unknown status")

            if (!this::value.isInitialized)
                throw IllegalArgumentException("Cannot build FieldResult without a value")

            return FieldResult(this.value, this.status, this.method)
        }
    }
}
