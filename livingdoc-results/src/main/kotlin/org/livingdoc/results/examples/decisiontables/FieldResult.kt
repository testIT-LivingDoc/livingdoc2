package org.livingdoc.results.examples.decisiontables

import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.results.Status
import java.lang.reflect.Method

data class FieldResult private constructor(
    val value: String,
    val status: Status,
    val method: Method?
) {
    /**
     * A not threadsafe builder class for [FieldResult] objects
     */
    class Builder {
        private lateinit var value: String
        private lateinit var status: Status
        private var method: Method? = null

        private var finalized = false

        private fun checkFinalized() {
            if (this.finalized)
                throw IllegalStateException(
                    "This FieldResult.Builder has already been finalized and can't be altered anymore."
                )
        }

        /**
         * Sets or overrides the value of a [DecisionTable] row that the built [FieldResult] refers to
         */
        fun withValue(value: String): Builder {
            checkFinalized()
            this.value = value
            return this
        }

        /**
         * Sets or overrides the status for the built [FieldResult]
         *
         * @param status Can be any [Status] except [Status.Unknown]
         */
        fun withStatus(status: Status): Builder {
            checkFinalized()
            this.status = status
            return this
        }

        /**
         * Sets or overrides the [check method][method] for the built [FieldResult]
         */
        fun withCheckMethod(method: Method): Builder {
            checkFinalized()
            this.method = method
            return this
        }

        /**
         * Build an immutable [FieldResult]
         *
         * WARNING: The builder will be finalized and can not be altered after calling this function
         *
         * @returns A new [FieldResult] with the data from this builder
         * @throws IllegalStateException If the builder is missing data to build a [FieldResult]
         */
        fun build(): FieldResult {
            // Finalize this builder. No further changes are allowed beyond this point
            this.finalized = true

            if (!this::status.isInitialized)
                throw IllegalStateException("Cannot build FieldResult with unknown status")
            val status = this.status

            if (!this::value.isInitialized)
                throw IllegalStateException("Cannot build FieldResult without a value")

            val value = this.value

            return FieldResult(value, status, method)
        }
    }
}
