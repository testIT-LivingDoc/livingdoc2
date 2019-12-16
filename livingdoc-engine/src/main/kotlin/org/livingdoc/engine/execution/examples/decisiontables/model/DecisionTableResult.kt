package org.livingdoc.engine.execution.examples.decisiontables.model

import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.TestDataResult
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.engine.execution.examples.scenarios.model.StepResult
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.repositories.model.scenario.Scenario
import kotlin.IllegalArgumentException

data class DecisionTableResult private constructor(
    val headers: List<Header>,
    val rows: List<RowResult>,
    val status: Status = Status.Unknown,
    val fixture: Fixture<DecisionTable>?,
    val decisionTable: DecisionTable
) : TestDataResult {

    class Builder {
        private var rows = mutableListOf<RowResult>()
        private var status: Status = Status.Unknown
        private var fixture: Fixture<DecisionTable>? = null
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
            when (this.status) {
                is Status.Unknown -> {
                    throw IllegalArgumentException("Cannot build DecisionTableResult with unknown status")
                }
                is Status.Manual, is Status.Disabled -> {
                    rows = decisionTable.rows.map {
                        RowResult.Builder()
                            .withRow(it)
                            .withStatus(this.status)
                            .build()
                    }.toMutableList()
                }
            }

            // Get headers
            val headers = mutableListOf<Header>()
            decisionTable.headers.forEach { (name) ->
                headers.add(Header(name))
            }

            // Build the result
            return DecisionTableResult(headers, this.rows, this.status, fixture, decisionTable)
        }
    }
}
