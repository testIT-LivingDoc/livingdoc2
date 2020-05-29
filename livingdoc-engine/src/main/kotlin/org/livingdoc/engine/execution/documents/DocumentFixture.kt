package org.livingdoc.engine.execution.documents

import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.tagging.Tag
import org.livingdoc.engine.DecisionTableToFixtureMatcher
import org.livingdoc.engine.ScenarioToFixtureMatcher
import org.livingdoc.engine.execution.groups.GroupFixtureModel
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult

/**
 * A DocumentFixture performs the binding between the actual LivingDoc document and the glue code to load and parse that
 * document
 */
internal class DocumentFixture(
    private val documentClass: Class<*>,
    private val repositoryManager: RepositoryManager,
    private val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher,
    private val scenarioToFixtureMatcher: ScenarioToFixtureMatcher,
    private val groupFixtureModel: GroupFixtureModel
) {
    private val documentIdentifier: DocumentIdentifier = DocumentIdentifier.of(this)

    /**
     * Execute runs the executable document described by this DocumentFixture
     *
     * @return a [DocumentResult] for this execution
     */
    fun execute(): DocumentResult {
        validate()

        val builder = DocumentResult.Builder()
        if (documentClass.isAnnotationPresent(Disabled::class.java)) {
            return builder.withDocumentClass(documentClass)
                .withTags(documentClass.getAnnotationsByType(Tag::class.java).map { it.value })
                .withStatus(Status.Disabled(documentClass.getAnnotation(Disabled::class.java).value)).build()
        }

        val document = loadDocument()

        return DocumentExecution(
            documentClass, document,
            decisionTableToFixtureMatcher, scenarioToFixtureMatcher, groupFixtureModel
        ).execute()
    }

    val executableDocumentAnnotation: ExecutableDocument?
        get() = documentClass.getAnnotation(ExecutableDocument::class.java)

    /**
     * Validate ensures that this instance represents a valid [DocumentFixture].
     */
    private fun validate() {
        if (executableDocumentAnnotation == null) {
            throw IllegalArgumentException(
                "ExecutableDocument annotation is not present on class ${documentClass.canonicalName}."
            )
        }
    }

    /**
     * LoadDocument loads the [Document] from the configured repositories.
     *
     * @return the loaded [Document]
     */
    private fun loadDocument(): Document {
        return with(documentIdentifier) {
            repositoryManager.getRepository(repository).getDocument(id)
        }
    }
}
