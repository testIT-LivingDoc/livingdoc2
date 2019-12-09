package org.livingdoc.engine.execution

import org.livingdoc.engine.execution.examples.TestDataResult

data class DocumentResult private constructor(
    val documentStatus: Status,
    val results: List<TestDataResult>
) {
    class Builder {
        private lateinit var status: Status
        private var results: MutableList<TestDataResult> = mutableListOf()

        fun withStatus(status: Status): Builder {
            this.status = status

            return this
        }

        fun withResult(result: TestDataResult): Builder {
            results.add(result)

            return this
        }

        fun build(): DocumentResult {
            return DocumentResult(status, results)
        }
    }
}
