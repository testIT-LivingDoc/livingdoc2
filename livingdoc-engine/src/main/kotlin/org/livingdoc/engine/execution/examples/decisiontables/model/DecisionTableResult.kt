package org.livingdoc.engine.execution.examples.decisiontables.model

import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.TestDataResult
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import java.util.*

data class DecisionTableResult private constructor(
    val headers: List<Header>,
    val rows: List<RowResult>,
    val status: Status = Status.Unknown,
    val fixture: Fixture<DecisionTable>?,
    val fixtureSource: Optional<Class<*>>,
    val decisionTable: DecisionTable
) : TestDataResult {

    class Builder {
        private val rows = mutableListOf<RowResult>()
        private lateinit var status: Status
        private var fixture: Fixture<DecisionTable>? = null
        private var fixtureSource = Optional.empty<Class<*>>()
        private var decisionTable: DecisionTable? = null

        /**
         * Sets the [RowResult] for a row in the given [DecisionTable]
         *
         * @param row A sucessfully built [RowResult]
         */
        fun withRow(row: RowResult): Builder {
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
            val decisionTable = this.decisionTable ?: throw IllegalStateException(
                "Cannot determine unmatched rows. A DecisionTable needs to be assigned to the builder first."
            )

            decisionTable.rows
                .filter { findMatchingRowResult(it) == null }
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

        private fun findMatchingRowResult(row: Row): RowResult? {
            return this.rows.firstOrNull {
                it.headerToField.none {
                    it.value.value != row.headerToField[it.key]?.value
                }
            }
        }

        /**
         * Sets or overrides the status for the built [DecisionTableResult]
         *
         * @param status Can be any [Status] except [Status.Unknown]
         */
        fun withStatus(status: Status): Builder {
            this.status = status
            return this
        }

        /**
         * Sets or overrides the [Fixture] that the built [DecisionTableResult] refers to
         */
        fun withFixture(fixture: Fixture<DecisionTable>): Builder {
            this.fixture = fixture
            return this
        }

        /**
         * Sets or overrides the [fixtureSource] that defines the implementation of [Fixture].
         * This value is optional.
         */
        fun withFixtureSource(fixtureSource: Class<*>): Builder {
            this.fixtureSource = Optional.of(fixtureSource)
            return this
        }

        /**
         * Sets or overrides the [DecisionTable] that the built [DecisionTableResult] refers to
         */
        fun withDecisionTable(decisionTable: DecisionTable): Builder {
            this.decisionTable = decisionTable
            return this
        }

        /**
         * Build an immutable [DecisionTableResult]
         *
         * @returns A new [DecisionTableResult] with the data from this builder
         * @throws IllegalStateException If the builder is missing data to build a [DecisionTableResult]
         */
        fun build(): DecisionTableResult {

            // TODO Can't add this check until execution is part of fixture class
            val fixture = this.fixture
            // ?: throw IllegalStateException("Cant't build DecisionTableResult without a fixture")

            val decisionTable = this.decisionTable
                ?: throw IllegalStateException("Cant't build DecisionTableResult without a decisionTable")

            // Check status
            if (!this::status.isInitialized) {
                throw IllegalArgumentException("Cannot build DecisionTableResult with unknown status")
            }
            val status = this.status
            val rows = if (status is Status.Manual || status is Status.Disabled)
                decisionTable.rows.map {
                    RowResult.Builder()
                        .withRow(it)
                        .withStatus(this.status)
                        .build()
                }.toMutableList()
            else
                this.rows

            // Get headers
            val headers = decisionTable.headers.map { (name) ->
                Header(name)
            }

            // Build the result
            return DecisionTableResult(headers, rows, status, fixture, fixtureSource, decisionTable)
        }
    }
}
