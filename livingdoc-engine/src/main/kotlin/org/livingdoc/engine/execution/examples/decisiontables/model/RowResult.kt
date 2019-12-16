package org.livingdoc.engine.execution.examples.decisiontables.model

import org.livingdoc.engine.execution.Status
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.HashMap

data class RowResult private constructor(
    val headerToField: Map<Header, FieldResult>,
    val status: Status = Status.Unknown,
    val fixtureMethod: Optional<Method>
) {
    class Builder {
        private var row: Row? = null
        private var fieldResults: MutableMap<Header, FieldResult> = HashMap()
        private var status: Status = Status.Unknown
        private var fixtureMethod: Method? = null

        /**
         * Sets the [FieldResult] for a given field in this row
         *
         * @param header Identifies the field of the [FieldResult]
         * @param field The given [FieldResult]
         */
        fun withFieldResult(header: Header, field: FieldResult): Builder {
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
         * Sets the status for the built [RowResult]
         */
        fun withStatus(status: Status): Builder {
            this.status = status
            return this
        }

        /**
         * Adds a fixture method that corresponds to the tested fixture method of this row
         */
        fun withFixtureMethod(method: Method): Builder {
            this.fixtureMethod = method
            return this
        }

        /**
         * Adds the corresponding [Row] for this [RowResult]
         */
        fun withRow(row: Row): Builder {
            this.row = row
            return this
        }

        /**
         * Marks all fields that have no result yet with [Status.Skipped]
         */
        fun withUnassignedFieldsSkipped(): Builder {
            when (this.row) {
                null -> {
                    throw IllegalStateException(
                        "Cannot determine unmatched fields. A Row needs to be assigned to the builder first."
                    )
                }
                else -> {
                    this.row!!.headerToField.forEach {
                        if (this.fieldResults[it.key] != null) {
                            return@forEach
                        }

                        withFieldResult(
                            it.key,
                            FieldResult.Builder()
                                .withValue(this.row!!.headerToField[it.key]!!.value)
                                .withStatus(Status.Skipped)
                                .build()
                        )
                    }
                }
            }
            return this
        }

        /**
         * Build an immutable [RowResult]
         *
         * @returns A new [RowResult] with the data from this builder
         * @throws IllegalStateException If the builder is missing data to build a [RowResult]
         */
        fun build(): RowResult {
            val headers = mutableListOf<Header>()
            when (this.row) {
                null -> {
                    throw IllegalStateException(
                        "Cannot build RowResult without a Row to match. Cannot determine required headers"
                    )
                }
                else -> {
                    this.row!!.headerToField.forEach { (header, _) ->
                        headers.add(Header(header.name))
                    }
                }
            }

            when (this.status) {
                Status.Unknown -> {
                    throw IllegalStateException("Cannot build RowResult with unknown status")
                }
                Status.Manual, is Status.Disabled -> {
                    headers.forEach {
                        this.fieldResults[it] = FieldResult.Builder()
                            .withValue(it.name)
                            .withStatus(this.status)
                            .build()
                    }
                }
            }

            val allowUnmatched = this.status is Status.Failed || this.status is Status.Exception
            val unmatchedHeaders = headers.filter {
                !this.fieldResults.containsKey(it)
            }
            if (!allowUnmatched && unmatchedHeaders.isNotEmpty()) {
                throw IllegalStateException(
                    "Cannot build RowResut. Not every header is matched with a value. Missing: $unmatchedHeaders"
                )
            }

            return when (this.fixtureMethod) {
                null -> {
                    RowResult(this.fieldResults, this.status, Optional.empty())
                }
                else -> {
                    RowResult(this.fieldResults, this.status, Optional.of(this.fixtureMethod!!))
                }
            }
        }
    }
}
