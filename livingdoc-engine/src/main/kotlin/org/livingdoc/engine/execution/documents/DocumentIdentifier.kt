package org.livingdoc.engine.execution.documents

internal data class DocumentIdentifier(
    val repository: String,
    val id: String
) {
    companion object {
        fun of(document: DocumentFixture): DocumentIdentifier {
            val annotation = document.executableDocumentAnnotation!!
            val values = annotation.value.split("://")
                    .also { require(it.size == 2) { "Illegal annotation value '${annotation.value}'." } }
            return DocumentIdentifier(values[0], values[1])
        }
    }
}
