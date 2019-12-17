package org.livingdoc.engine.execution.documents

import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.engine.DecisionTableToFixtureMatcher
import org.livingdoc.engine.ScenarioToFixtureMatcher
import org.livingdoc.engine.execution.Status
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.RepositoryManager

internal class DocumentFixture(
    private val documentClass: Class<*>,
    private val repositoryManager: RepositoryManager,
    private val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher,
    private val scenarioToFixtureMatcher: ScenarioToFixtureMatcher
) {
    private val documentIdentifier: DocumentIdentifier = DocumentIdentifier.of(this)

    fun execute(): DocumentResult {
        validate()

        val builder = DocumentResult.Builder()
        if (documentClass.isAnnotationPresent(Disabled::class.java)) {
            return builder.withDocumentClass(documentClass)
                .withStatus(Status.Disabled(documentClass.getAnnotation(Disabled::class.java).value)).build()
        }

        val document = loadDocument()

        return DocumentExecution(documentClass, document,
            decisionTableToFixtureMatcher, scenarioToFixtureMatcher).execute()
    }

    val executableDocumentAnnotation: ExecutableDocument?
        get() = documentClass.getAnnotation(ExecutableDocument::class.java)

    private fun validate() {
        if (executableDocumentAnnotation == null) {
            throw IllegalArgumentException(
                    "ExecutableDocument annotation is not present on class ${documentClass.canonicalName}."
            )
        }
    }

    private fun loadDocument(): Document {
        return with(documentIdentifier) {
            repositoryManager.getRepository(repository).getDocument(id)
        }
    }
}
