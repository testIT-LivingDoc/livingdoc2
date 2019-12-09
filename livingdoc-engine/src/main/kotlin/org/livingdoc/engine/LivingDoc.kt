package org.livingdoc.engine

import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture
import org.livingdoc.config.ConfigProvider
import org.livingdoc.engine.execution.DocumentResult
import org.livingdoc.engine.execution.ExecutionException
import org.livingdoc.engine.execution.Status
import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableFixtureWrapper
import org.livingdoc.engine.execution.examples.scenarios.ScenarioFixtureWrapper
import org.livingdoc.engine.fixtures.FixtureMethodInvoker
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.repositories.config.RepositoryConfiguration
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.scenario.Scenario
import java.lang.reflect.Method
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
    val configProvider: ConfigProvider = ConfigProvider.load(),
    val repositoryManager: RepositoryManager = RepositoryManager.from(RepositoryConfiguration.from(configProvider)),
    private val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher = DecisionTableToFixtureMatcher(),
    private val scenarioToFixtureMatcher: ScenarioToFixtureMatcher = ScenarioToFixtureMatcher()
) {

    private lateinit var documentClass: Class<*>
    private lateinit var documentClassModel: ExecutableDocumentModel
    private lateinit var document: Document
    private lateinit var methodInvoker: FixtureMethodInvoker

    @Throws(ExecutionException::class)
    fun execute(documentClass: Class<*>): DocumentResult {
        val builder = DocumentResult.Builder()
        if (documentClass.isAnnotationPresent(Disabled::class.java)) {
            return builder.withStatus(Status.Disabled(documentClass.getAnnotation(Disabled::class.java).value)).build()
        }

        this.documentClass = documentClass
        documentClassModel = ExecutableDocumentModel.of(documentClass)
        document = loadDocument(documentClassModel)
        methodInvoker = FixtureMethodInvoker(document)

        executeDocument(builder)

        return builder.build()
    }

    private fun executeDocument(builder: DocumentResult.Builder) {
        val documentInstance = createDocumentInstance()
        invokeBeforeMethods(documentInstance)
        executeFixtures(builder)
        builder.withStatus(Status.Executed)
        invokeAfterMethods(documentInstance)
    }

    private fun createDocumentInstance(): Any {
        return documentClass.getConstructor().newInstance()
    }

    private fun invokeBeforeMethods(documentInstance: Any) {
        documentClassModel.beforeMethods.forEach { method -> methodInvoker.invoke(method, documentInstance) }
    }

    private fun executeFixtures(builder: DocumentResult.Builder) {
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
    }

    private fun invokeAfterMethods(documentInstance: Any) {
        documentClassModel.afterMethods.forEach { method -> methodInvoker.invoke(method, documentInstance) }
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
    val decisionTableFixtures: List<DecisionTableFixtureWrapper>,
    val scenarioFixtures: List<ScenarioFixtureWrapper>,
    val beforeMethods: List<Method>,
    val afterMethods: List<Method>
) {

    companion object {

        fun of(documentClass: Class<*>): ExecutableDocumentModel {
            validate(documentClass)
            return ExecutableDocumentModel(
                    documentIdentifier = getDocumentIdentifier(documentClass),
                    decisionTableFixtures = getDecisionTableFixtures(documentClass),
                    scenarioFixtures = getScenarioFixtures(documentClass),
                    beforeMethods = getBeforeMethods(documentClass),
                    afterMethods = getAfterMethods(documentClass)
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

        private fun getDecisionTableFixtures(document: Class<*>): List<DecisionTableFixtureWrapper> {
            return getFixtures(document, DecisionTableFixture::class).map {
                DecisionTableFixtureWrapper(it)
            }
        }

        private fun getScenarioFixtures(document: Class<*>): List<ScenarioFixtureWrapper> {
            return getFixtures(document, ScenarioFixture::class).map {
                ScenarioFixtureWrapper(it)
            }
        }

        private fun getAfterMethods(document: Class<*>): List<Method> {
            return document.methods.filter { method -> method.isAnnotationPresent(After::class.java) }
        }

        private fun getBeforeMethods(document: Class<*>): List<Method> {
            return document.methods.filter { method -> method.isAnnotationPresent(Before::class.java) }
        }

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
