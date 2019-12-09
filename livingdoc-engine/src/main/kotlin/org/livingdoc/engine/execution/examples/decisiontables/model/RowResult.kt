package org.livingdoc.engine.execution.examples.decisiontables.model

import org.livingdoc.engine.execution.Status
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Header
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class RowResult private constructor(
    val headerToField: Map<Header, FieldResult>,
    val status: Status = Status.Unknown,
    val fixtureMethod: Optional<Method>
) {
    class Builder {
        private var headers: MutableList<Header> = ArrayList()
        private var fieldResults: MutableMap<Header, FieldResult> = HashMap()
        private var status: Status = Status.Unknown
        private var fixtureMethod: Method? = null

        fun withFieldResult(header: Header, result: FieldResult): Builder {
            this.fieldResults.put(header, result)
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

        fun withDecisionTable(decisionTable: DecisionTable): Builder {
            decisionTable.headers.forEach { (name) ->
                this.headers.add(Header(name))
            }
            return this
        }

        fun build(): RowResult {
            if (this.headers.size == 0) {
                throw IllegalArgumentException(
                    "Cannot build RowResult without a decision table to match. Cannot determine required headers"
                )
            }

            val unmatchedHeaders = this.headers.filter {
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
