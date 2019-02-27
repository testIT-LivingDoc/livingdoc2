package org.livingdoc.engine

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.engine.execution.DocumentResult
import org.livingdoc.engine.execution.ExecutionException
import org.livingdoc.engine.execution.examples.ExampleResult
import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableExecutor
import org.livingdoc.engine.execution.examples.scenarios.ScenarioExecutor
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.repositories.config.Configuration
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.scenario.Scenario
import kotlin.reflect.KClass

/**
 * Executes the given document class and returns the [DocumentResult]. The document's class must be annotated
 * with [ExecutableDocument].
 *
 * @return the [DocumentResult] of the execution
 * @throws ExecutionException in case the execution failed in a way that did not produce a viable result
 * @since 2.0
 */
class LivingDoc(
    val repositoryManager: RepositoryManager = RepositoryManager.from(Configuration.load()),
    val decisionTableExecutor: DecisionTableExecutor = DecisionTableExecutor(),
    val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher = DecisionTableToFixtureMatcher(),
    val scenarioExecutor: ScenarioExecutor = ScenarioExecutor(),
    val scenarioToFixtureMatcher: ScenarioToFixtureMatcher = ScenarioToFixtureMatcher()
) {

    @Throws(ExecutionException::class)
    fun execute(documentClass: Class<*>): DocumentResult {
        val documentClassModel = ExecutableDocumentModel.of(documentClass)

        val document = loadDocument(documentClassModel)

        val results: List<ExampleResult> = document.elements.mapNotNull { element ->
            when (element) {
                is DecisionTable -> {
                    decisionTableToFixtureMatcher
                        .findMatchingFixture(element, documentClassModel.decisionTableFixtures)
                        ?.let { decisionTableExecutor.execute(element, it) }
                }
                is Scenario -> {
                    scenarioToFixtureMatcher.findMatchingFixture(element, documentClassModel.scenarioFixtures)
                        ?.let { scenarioExecutor.execute(element, it) }
                }
                else -> null
            }
        }

        return DocumentResult(results)
    }

    private fun loadDocument(documentClassModel: ExecutableDocumentModel): Document {
        return with(documentClassModel.documentIdentifier) {
            repositoryManager.getRepository(repository).getDocument(id)
        }
    }
}

private data class DocumentIdentifier(
    val repository: String,
    val id: String
)

private data class ExecutableDocumentModel(
    val documentIdentifier: DocumentIdentifier,
    val decisionTableFixtures: List<Class<*>>,
    val scenarioFixtures: List<Class<*>>
) {

    companion object {

        fun of(documentClass: Class<*>): ExecutableDocumentModel {
            validate(documentClass)
            return ExecutableDocumentModel(
                documentIdentifier = getDocumentIdentifier(documentClass),
                decisionTableFixtures = getDecisionTableFixtures(documentClass),
                scenarioFixtures = getScenarioFixtures(documentClass)
            )
        }

        private fun getDocumentIdentifier(document: Class<*>): DocumentIdentifier {
            val annotation = document.executableDocumentAnnotation!!
            val values = annotation.value.split("://")
                .also { require(it.size == 2) { "Illegal annotation value '${annotation.value}'." } }
            return DocumentIdentifier(values[0], values[1])
        }

        private fun validate(document: Class<*>) {
            if (document.executableDocumentAnnotation == null) {
                throw IllegalArgumentException(
                    "ExecutableDocument annotation is not present on class ${document.canonicalName}."
                )
            }
        }

        private fun getDecisionTableFixtures(document: Class<*>) =
            getFixtures(document, DecisionTableFixture::class)

        private fun getScenarioFixtures(document: Class<*>) =
            getFixtures(document, ScenarioFixture::class)

        private fun getFixtures(document: Class<*>, annotationClass: KClass<out Annotation>): List<Class<*>> {
            val declaredInside = document.declaredClasses
                .filter { it.isAnnotationPresent(annotationClass.java) }
            val fromAnnotation = document.executableDocumentAnnotation!!.fixtureClasses
                .map { it.java }
                .filter { it.isAnnotationPresent(annotationClass.java) }
            return declaredInside + fromAnnotation
        }

        private val Class<*>.executableDocumentAnnotation: ExecutableDocument?
            get() = getAnnotation(ExecutableDocument::class.java)
    }
}
