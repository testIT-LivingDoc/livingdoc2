package org.livingdoc.results.documents

import org.livingdoc.repositories.model.TestData
import org.livingdoc.results.Status
import org.livingdoc.results.TestDataResult
import java.time.Duration

/**
 * A DocumentResult is the result obtained from a single DocumentExecution.
 */
data class DocumentResult
private constructor(
    val documentClass: Class<*>,
    val documentStatus: Status,
    val results: List<TestDataResult<TestData>>,
    val tags: List<String>,
    val time: Duration
) {
    /**
     * Builder can be used to build a [DocumentResult].
     */
    class Builder {
        private var time: Duration = Duration.ofMillis(0)
        private lateinit var documentClass: Class<*>
        private lateinit var status: Status
        private lateinit var tags: List<String>
        private var results: MutableList<TestDataResult<TestData>> = mutableListOf()

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
        fun withResult(result: TestDataResult<TestData>): Builder {
            results.add(result)

            return this
        }

        /**
         * Build creates a [DocumentResult] from the data contained in this Builder
         *
         * @return a [DocumentResult] containing the data of this Builder
         */
        fun build(): DocumentResult {
            return DocumentResult(documentClass, status, results, tags, time)
        }

        /**
         * Set the tags that were applied to the document
         */
        fun withTags(tags: List<String>): Builder {
            this.tags = tags

            return this
        }

        fun withTime(time: Duration): Builder {
            this.time = time

            return this
        }
    }
}
