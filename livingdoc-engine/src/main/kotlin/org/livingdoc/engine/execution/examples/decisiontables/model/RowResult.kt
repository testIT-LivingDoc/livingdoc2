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

        fun withFieldResult(header: Header, result: FieldResult): Builder {
            this.fieldResults[header] = result
            return this
        }

        fun withStatus(status: Status): Builder {
            this.status = status
            return this
        }

        fun withFixtureMethod(method: Method): Builder {
            this.fixtureMethod = method
            return this
        }

        fun withRow(row: Row): Builder {
            this.row = row
            return this
        }

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

        fun build(): RowResult {
            val headers = mutableListOf<Header>()
            when (this.row) {
                null -> {
                    throw IllegalArgumentException(
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
                    throw IllegalArgumentException("Cannot build RowResult with unknown status")
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

            val unmatchedHeaders = headers.filter {
                !this.fieldResults.containsKey(it)
            }
            if (unmatchedHeaders.isNotEmpty()) {
                throw IllegalArgumentException(
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
