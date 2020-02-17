package org.livingdoc.jvm.engine

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.jvm.api.extension.context.DocumentFixtureContext
import org.livingdoc.jvm.engine.manager.ExtensionManager
import org.livingdoc.jvm.engine.manager.FixtureManager
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
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
        extensionManager.loadExtensions(context)

        val extensionContext = context.extensionContext as DocumentFixtureContext
        val resultBuilder = DocumentResult.Builder().withDocumentClass(extensionContext.documentFixtureClass.java)

        if (!extensionManager.shouldExecute(context)) {
            return resultBuilder.withStatus(Status.Disabled()).build()
        }

        extensionManager.executeBeforeDocumentFixture(context)

        val documentInformation = extensionContext.documentInformation

        val document = repositoryManager.getRepository(extractRepositoryName(documentInformation))
            .getDocument(extractDocumentId(documentInformation))

        val results = document.elements.map {
            val fixture = fixtureManager.getFixture(context, it)
            fixture.execute(it)
        }
        extensionManager.executeAfterDocumentFixture(context)

        val result =
            resultBuilder.withStatus(Status.Executed)
        results.forEach {
            result.withResult(it)
        }

        return result.build()
    }
}

val DocumentFixtureContext.documentInformation: String
    get() = this.documentFixtureClass.findAnnotation<ExecutableDocument>()!!.value

fun extractRepositoryName(documentInformation: String) = documentInformation.substringBefore("://")

fun extractDocumentId(documentInformation: String) = documentInformation.substringAfter("://")
