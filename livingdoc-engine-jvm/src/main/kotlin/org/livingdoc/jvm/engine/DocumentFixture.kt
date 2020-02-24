package org.livingdoc.jvm.engine

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.jvm.api.extension.context.DocumentFixtureContext
import org.livingdoc.jvm.api.extension.context.GroupContext
import org.livingdoc.jvm.engine.config.getTags
import org.livingdoc.jvm.engine.extension.context.DocumentFixtureContextImpl
import org.livingdoc.jvm.engine.manager.ExtensionManager
import org.livingdoc.jvm.engine.manager.ExtensionRegistryImpl
import org.livingdoc.jvm.engine.manager.FixtureManager
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

internal class DocumentFixture(
    private val context: EngineContext,
    private val repositoryManager: RepositoryManager,
    private val fixtureManager: FixtureManager,
    private val extensionManager: ExtensionManager
) {
    /**
     * Execute runs the executable document described by this DocumentFixture
     *
     * @return a [DocumentResult] for this execution
     */
    fun execute(): DocumentResult {

        val extensionContext = context.extensionContext as DocumentFixtureContext
        val resultBuilder =
            DocumentResult.Builder().withDocumentClass(extensionContext.documentFixtureClass.java).withTags(
                getTags(extensionContext.documentFixtureClass)
            )

        val conditionEvaluationResult = extensionManager.shouldExecute(context)
        if (conditionEvaluationResult.disabled) {
            return resultBuilder.withStatus(Status.Disabled(conditionEvaluationResult.reason.orEmpty())).build()
        }

        extensionManager.executeBeforeDocumentFixture(context)

        val documentInformation = extensionContext.documentInformation

        val document = repositoryManager.getRepository(extractRepositoryName(documentInformation))
            .getDocument(extractDocumentId(documentInformation)) // TODO Handle exception

        val results = document.elements.filter { !it.description.isManual }.map {
            // TODO manual
            val fixture = fixtureManager.getFixture(context, it, extensionManager)
            fixture.execute(it)
        }
        extensionManager.executeAfterDocumentFixture(context)

        val result = resultBuilder.withStatus(Status.Executed)
        results.forEach {
            result.withResult(it)
        }

        return result.build()
    }

    companion object {
        fun createContext(documentFixtureClass: KClass<*>, parent: EngineContext): EngineContext {
            val documentFixtureContext =
                DocumentFixtureContextImpl(
                    documentFixtureClass,
                    parent.extensionContext as GroupContext
                )
            val extensionRegistry =
                ExtensionRegistryImpl.createRegistryFrom(
                    ExtensionRegistryImpl.loadExtensions(documentFixtureClass),
                    parent.extensionRegistry
                )
            return EngineContext(parent, documentFixtureContext, extensionRegistry)
        }
    }
}

val DocumentFixtureContext.documentInformation: String
    get() = this.documentFixtureClass.findAnnotation<ExecutableDocument>()!!.value

fun extractRepositoryName(documentInformation: String) = documentInformation.substringBefore("://")

fun extractDocumentId(documentInformation: String) = documentInformation.substringAfter("://")
