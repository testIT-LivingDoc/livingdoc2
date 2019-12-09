package org.livingdoc.engine.execution.examples.decisiontables.model

import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.TestDataResult
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Header
import java.lang.IllegalArgumentException

data class DecisionTableResult private constructor(
    val headers: List<Header>,
    val rows: List<RowResult>,
    val status: Status = Status.Unknown,
    val fixture: Fixture<DecisionTable>?,
    val decisionTable: DecisionTable
) : TestDataResult {

    class Builder {
        private var rows: MutableList<RowResult> = ArrayList()
        private var status: Status = Status.Unknown
        private var fixture: Fixture<DecisionTable>? = null
        private var decisionTable: DecisionTable? = null

        fun withRow(row: RowResult): Builder {
            this.rows.add(row)
            return this
        }

        fun withStatus(status: Status): Builder {
            this.status = status
            return this
        }

        fun withFixture(fixture: Fixture<DecisionTable>): Builder {
            this.fixture = fixture
            return this
        }

        fun withDecisionTable(decisionTable: DecisionTable): Builder {
            this.decisionTable = decisionTable
            return this
        }

        fun build(): DecisionTableResult {

            when {
                this.fixture == null -> {
                    // TODO Can't add this check until execution is part of fixture class
                    // throw IllegalArgumentException("Cant't build DecisionTableResult without a fixture")
                }
                this.decisionTable == null -> {
                    throw IllegalArgumentException("Cant't build DecisionTableResult without a decisionTable")
                }
            }

            when (this.status) {
                is Status.Unknown -> {
                    // Retrieve status from rows
                    status = if (rows.filter {
                            it.status !is Status.Executed
                        }.isEmpty()) Status.Executed else Status.Skipped
                }
                is Status.Manual, is Status.Disabled -> {
                    rows = decisionTable!!.rows.map {
                        RowResult.Builder()
                            .withStatus(this.status)
                            .build()
                    }.toMutableList()
                }
            }

            val headers = mutableListOf<Header>()
            this.decisionTable!!.headers.forEach { (name) ->
                headers.add(Header(name))
            }

            return DecisionTableResult(headers, this.rows, this.status, this.fixture, this.decisionTable!!)
        }
    }
}
