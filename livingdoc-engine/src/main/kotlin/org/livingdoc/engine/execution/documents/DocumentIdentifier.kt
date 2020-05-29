package org.livingdoc.engine.execution.documents

/**
 * A DocumentIdentifier contains the storage location and name of a single document
 */
internal data class DocumentIdentifier(
    val repository: String,
    val id: String
) {
    companion object {
        /**
         * of creates a DocumentIdentifier from a [DocumentFixture].
         *
         * @param document the [DocumentFixture] for which to create the DocumentIdentifier
         * @return the DocumentIdentifier for the given [DocumentFixture]
         */
        fun of(document: DocumentFixture): DocumentIdentifier {
            val annotation = document.executableDocumentAnnotation!!
            val values = annotation.value.split("://")
                    .also { require(it.size == 2) { "Illegal annotation value '${annotation.value}'." } }
            return DocumentIdentifier(values[0], values[1])
        }
    }
}
