package org.livingdoc.results.examples.decisiontables

import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.results.Status

data class RowResult private constructor(
    val headerToField: Map<Header, FieldResult>,
    val status: Status = Status.Unknown
) {
    /**
     * A not threadsafe builder class for [RowResult] objects
     */
    class Builder {
        private var row: Row? = null
        private val fieldResults: MutableMap<Header, FieldResult> = HashMap()
        private lateinit var status: Status

        private var finalized = false

        private fun checkFinalized() {
            if (this.finalized)
                throw IllegalStateException(
                    "This RowResult.Builder has already been finalized and can't be altered anymore."
                )
        }

        /**
         * Sets the [FieldResult] for a given field in this row
         *
         * @param header Identifies the field of the [FieldResult]
         * @param field The given [FieldResult]
         */
        fun withFieldResult(header: Header, field: FieldResult): Builder {
            checkFinalized()

            this.fieldResults[header] = field
            when (field.status) {
                is Status.Exception -> {
                    this.status = Status.Exception(field.status.exception)
                }
                is Status.Failed -> {
                    this.status = Status.Failed(field.status.reason)
                }
            }
            return this
        }

        /**
         * Sets or overrides the status for the built [RowResult]
         *
         * @param status Can be any [Status] except [Status.Unknown]
         */
        fun withStatus(status: Status): Builder {
            checkFinalized()
            this.status = status
            return this
        }

        /**
         * Sets or overrides the [Row] that the built [RowResult] refers to
         */
        fun withRow(row: Row): Builder {
            checkFinalized()
            this.row = row
            return this
        }

        /**
         * Marks all fields that have no result yet with [Status.Skipped]
         */
        fun withUnassignedFieldsSkipped(): Builder {
            checkFinalized()

            val row = this.row ?: throw IllegalStateException(
                "Cannot determine unmatched fields. A Row needs to be assigned to the builder first."
            )

            row.headerToField
                .filter { this.fieldResults[it.key] == null }
                .forEach {
                    withFieldResult(
                        it.key,
                        FieldResult.Builder()
                            .withValue(row.headerToField[it.key]!!.value)
                            .withStatus(Status.Skipped)
                            .build()
                    )
                }

            return this
        }

        /**
         * Build an immutable [RowResult]
         *
         * WARNING: The builder will be finalized and can not be altered after calling this function
         *
         * @returns A new [RowResult] with the data from this builder
         * @throws IllegalStateException If the builder is missing data to build a [RowResult]
         */
        fun build(): RowResult {
            // Finalize this builder. No further changes are allowed
            this.finalized = true

            // Read headers
            val row = this.row ?: throw IllegalStateException(
                "Cannot build RowResult without a Row to match. Cannot determine required headers"
            )
            val headers = row.headerToField.map { (header, _) ->
                Header(header.name)
            }

            // Validate status
            if (!this::status.isInitialized) {
                throw IllegalStateException("Cannot build RowResult with unknown status")
            }
            val status = this.status
            val fieldResults = if (status is Status.Manual || status is Status.Disabled)
                headers.map {
                    it to FieldResult.Builder()
                        .withValue(row.headerToField[it]?.value!!)
                        .withStatus(this.status)
                        .build()
                }.toMap()
            else this.fieldResults

            // Check whether all fields have a valid result
            val unmatchedHeaders = headers.filter {
                !fieldResults.containsKey(it)
            }
            if (fieldResults.size != headers.size) {
                throw IllegalStateException(
                    "Cannot build RowResult. The number of field results (${fieldResults.size})" +
                            " does not match the expected number (${headers.size})"
                )
            } else if (unmatchedHeaders.isNotEmpty()) {
                throw IllegalStateException(
                    "Cannot build RowResult. Not every header is matched with a value. Missing: $unmatchedHeaders"
                )
            }

            // Build result
            return RowResult(fieldResults, status)
        }
    }
}
