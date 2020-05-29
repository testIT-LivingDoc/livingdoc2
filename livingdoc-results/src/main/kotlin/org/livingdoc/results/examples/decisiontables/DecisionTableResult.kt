package org.livingdoc.results.examples.decisiontables

import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.results.Status
import org.livingdoc.results.TestDataResult

data class DecisionTableResult private constructor(
    val headers: List<Header>,
    val rows: List<RowResult>,
    override val status: Status = Status.Unknown,
    val fixtureSource: Class<*>?,
    val decisionTable: DecisionTable
) : TestDataResult<DecisionTable> {
    /**
     * A not threadsafe builder class for [DecisionTableResult] objects
     */
    class Builder {
        private val rows = mutableListOf<RowResult>()
        private lateinit var status: Status
        private var fixtureSource: Class<*>? = null
        private var decisionTable: DecisionTable? = null

        private var finalized = false

        private fun checkFinalized() {
            if (this.finalized)
                throw IllegalStateException(
                    "This DecisionTableResult.Builder has already been finalized and can't be altered anymore."
                )
        }

        /**
         * Sets the [RowResult] for a row in the given [DecisionTable]
         *
         * **NOTE** The row results must be added in the same order in which they are defined in the decision table.
         *
         * @param row A successfully built [RowResult]
         */
        fun withRow(row: RowResult): Builder {
            checkFinalized()
            this.rows.add(row)
            when (row.status) {
                is Status.Exception -> {
                    this.status = Status.Exception(row.status.exception)
                }
                is Status.Failed -> {
                    this.status = Status.Failed(row.status.reason)
                }
            }
            return this
        }

        /**
         * Marks all rows that have no result yet with [Status.Skipped]
         */
        fun withUnassignedRowsSkipped(): Builder {
            checkFinalized()
            val decisionTable = this.decisionTable ?: throw IllegalStateException(
                "Cannot determine unmatched rows. A DecisionTable needs to be assigned to the builder first."
            )

            decisionTable.rows.subList(this.rows.size, decisionTable.rows.size)
                .forEach {
                    this.withRow(
                        RowResult.Builder()
                            .withStatus(Status.Skipped)
                            .withRow(it)
                            .withUnassignedFieldsSkipped()
                            .build()
                    )
                }
            return this
        }

        private fun matchesRowResult(row: Row, result: RowResult): Boolean {
            return row.headerToField.all { (header, field) ->
                result.headerToField[header]?.value == field.value
            }
        }

        /**
         * Sets or overrides the status for the built [DecisionTableResult]
         *
         * @param status Can be any [Status] except [Status.Unknown]
         */
        fun withStatus(status: Status): Builder {
            checkFinalized()
            this.status = status
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
         * Sets or overrides the [DecisionTable] that the built [DecisionTableResult] refers to
         */
        fun withDecisionTable(decisionTable: DecisionTable): Builder {
            checkFinalized()
            this.decisionTable = decisionTable
            return this
        }

        /**
         * Build an immutable [DecisionTableResult]
         *
         * WARNING: The builder will be finalized and can not be altered after calling this function
         *
         * @returns A new [DecisionTableResult] with the data from this builder
         * @throws IllegalStateException If the builder is missing data to build a [DecisionTableResult]
         */
        fun build(): DecisionTableResult {
            // Finalize this builder. No further changes are allowed
            this.finalized = true

            val decisionTable = this.decisionTable
                ?: throw IllegalStateException("Cant't build DecisionTableResult without a decisionTable")

            // Check status
            if (!this::status.isInitialized) {
                throw IllegalStateException("Cannot build DecisionTableResult with unknown status")
            }
            val status = this.status
            val rows = if (status is Status.Manual || status is Status.Disabled)
                decisionTable.rows.map {
                    RowResult.Builder()
                        .withRow(it)
                        .withStatus(status)
                        .build()
                }.toList()
            else
                this.rows

            // Check rows
            if (rows.size != decisionTable.rows.size) {
                throw IllegalStateException(
                    "Cannot build DecisionTableResult. The number of row results (${rows.size})" +
                            " does not match the expected number (${decisionTable.rows.size})"
                )
            }

            if (decisionTable.rows.zip(rows).any { (row, result) ->
                    !matchesRowResult(row, result)
                }) {
                throw IllegalStateException("Not all decision table rows are contained in the result")
            }

            // Get headers
            val headers = decisionTable.headers.map { (name) ->
                Header(name)
            }

            // Build the result
            return DecisionTableResult(
                headers,
                rows,
                status,
                fixtureSource,
                decisionTable
            )
        }
    }
}
