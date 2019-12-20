package org.livingdoc.engine.execution.documents

import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.TestDataResult
import org.livingdoc.repositories.model.TestData

/**
 * A DocumentResult is the result obtained from a single [DocumentExecution].
 *
 * @see DocumentExecution
 */
data class DocumentResult private constructor(
    val documentClass: Class<*>,
    val documentStatus: Status,
    val results: List<TestDataResult<out TestData>>
) {
    /**
     * Builder can be used to build a [DocumentResult].
     */
    class Builder {
        private lateinit var documentClass: Class<*>
        private lateinit var status: Status
        private var results: MutableList<TestDataResult<out TestData>> = mutableListOf()

        /**
         * WithDocumentClass sets the document class for which to build the [DocumentResult].
         *
         * @param documentClass the document class
         * @return a Builder instance with the documentClass property set
         */
        fun withDocumentClass(documentClass: Class<*>): Builder {
            this.documentClass = documentClass

            return this
        }

        /**
         * WithStatus sets the status for the [DocumentResult] to build
         *
         * @param status the status of the execution
         * @return a Builder instance with the status property set
         */
        fun withStatus(status: Status): Builder {
            this.status = status

            return this
        }

        /**
         * WithResult adds a new [TestDataResult] to the results for this execution.
         *
         * @param result the [TestDataResult] to add
         * @return a Builder instance containing the new result
         */
        fun withResult(result: TestDataResult<out TestData>): Builder {
            results.add(result)

            return this
        }

        /**
         * Build creates a [DocumentResult] from the data contained in this Builder
         *
         * @return a [DocumentResult] containing the data of this Builder
         */
        fun build(): DocumentResult {
            return DocumentResult(documentClass, status, results)
        }
    }
}
