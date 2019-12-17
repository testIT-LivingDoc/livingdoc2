package org.livingdoc.engine.execution

import org.livingdoc.engine.execution.examples.TestDataResult

data class DocumentResult private constructor(
    val documentClass: Class<*>,
    val documentStatus: Status,
    val results: List<TestDataResult>
) {
    class Builder {
        private lateinit var documentClass: Class<*>
        private lateinit var status: Status
        private var results: MutableList<TestDataResult> = mutableListOf()

        fun withDocumentClass(documentClass: Class<*>): Builder {
            this.documentClass = documentClass

            return this
        }

        fun withStatus(status: Status): Builder {
            this.status = status

            return this
        }

        fun withResult(result: TestDataResult): Builder {
            results.add(result)

            return this
        }

        fun build(): DocumentResult {
            return DocumentResult(documentClass, status, results)
        }
    }
}
