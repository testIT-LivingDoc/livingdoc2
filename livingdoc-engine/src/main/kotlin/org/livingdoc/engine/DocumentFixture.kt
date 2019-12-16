package org.livingdoc.engine

import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.engine.execution.DocumentResult
import org.livingdoc.engine.execution.Status
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.scenario.Scenario

internal class DocumentFixture(
    val documentClass: Class<*>,
    val repositoryManager: RepositoryManager,
    val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher,
    val scenarioToFixtureMatcher: ScenarioToFixtureMatcher
) {
    val documentIdentifier: DocumentIdentifier = DocumentIdentifier.of(this)

    fun execute(): DocumentResult {
        validate()

        val builder = DocumentResult.Builder()
        if (documentClass.isAnnotationPresent(Disabled::class.java)) {
            return builder.withStatus(Status.Disabled(documentClass.getAnnotation(Disabled::class.java).value)).build()
        }

        val document = loadDocument()

        val documentClassModel = DocumentFixtureModel(documentClass)

        document.elements.mapNotNull { element ->
            when (element) {
                is DecisionTable -> {
                    decisionTableToFixtureMatcher
                            .findMatchingFixture(element, documentClassModel.decisionTableFixtures)
                            .execute(element)
                }
                is Scenario -> {
                    scenarioToFixtureMatcher
                            .findMatchingFixture(element, documentClassModel.scenarioFixtures)
                            .execute(element)
                }
                else -> null
            }
        }.forEach { result -> builder.withResult(result) }

        val result = builder.withStatus(Status.Executed).build()

        return DocumentResult.Builder().withStatus(Status.Skipped).build()
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
