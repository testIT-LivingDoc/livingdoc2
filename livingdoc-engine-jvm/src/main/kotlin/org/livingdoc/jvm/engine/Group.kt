package org.livingdoc.jvm.engine

import org.livingdoc.jvm.engine.extension.GroupContextImpl
import org.livingdoc.jvm.engine.manager.ExtensionManager
import org.livingdoc.jvm.engine.manager.FixtureManager
import org.livingdoc.jvm.engine.manager.loadExtensions
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import kotlin.reflect.KClass

internal class Group(
    private val context: EngineContext,
    private val documentClasses: List<KClass<*>>,
    private val repositoryManager: RepositoryManager,
    private val fixtureManager: FixtureManager,
    private val extensionManager: ExtensionManager
) {
    fun execute(): List<DocumentResult> {

        val conditionEvaluationResult = extensionManager.shouldExecute(context)
        if (conditionEvaluationResult.disabled) {
            return documentClasses.resultsWithStatus(Status.Disabled(conditionEvaluationResult.reason.orEmpty()))
        }

        val throwableCollector = context.throwableCollector

        extensionManager.executeBeforeGroup(context)
        if (!throwableCollector.isEmpty()) {
            return documentClasses.resultsWithStatus(Status.Exception(throwableCollector.throwable))
        }

        val results = documentClasses.map {
            val documentFixtureEngineContext = DocumentFixture.createContext(it, context)
            val documentFixture =
                DocumentFixture(documentFixtureEngineContext, repositoryManager, fixtureManager, extensionManager)
            documentFixture.execute()
        }
        extensionManager.executeAfterGroup(context)
        if (!throwableCollector.isEmpty()) {
            return documentClasses.resultsWithStatus(Status.Exception(throwableCollector.throwable))
        }

        return results
    }

    companion object {
        fun createContext(groupClass: KClass<*>): EngineContext {
            val extensionContext = GroupContextImpl(groupClass)
            return EngineContext(null, extensionContext, loadExtensions(groupClass))
        }
    }
}

fun List<KClass<*>>.resultsWithStatus(status: Status): List<DocumentResult> =
    map { DocumentResult.Builder().withDocumentClass(it.java).withStatus(status).build() }
