package org.livingdoc.jvm.engine

import org.livingdoc.jvm.engine.config.getTags
import org.livingdoc.jvm.engine.extension.context.GroupContextImpl
import org.livingdoc.jvm.engine.manager.ExtensionManager
import org.livingdoc.jvm.engine.manager.ExtensionRegistryImpl
import org.livingdoc.jvm.engine.manager.FixtureManager
import org.livingdoc.jvm.engine.result.GroupResultImpl
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.groups.GroupResult
import kotlin.reflect.KClass

internal class Group(
    private val context: EngineContext,
    private val documentClasses: List<KClass<*>>,
    private val repositoryManager: RepositoryManager,
    private val fixtureManager: FixtureManager,
    private val extensionManager: ExtensionManager
) {
    @Suppress("ReturnCount")
    fun execute(): GroupResult {
        val conditionEvaluationResult = extensionManager.shouldExecute(context)
        if (conditionEvaluationResult.disabled) {
            return createResultWithDocumentResultsSkipped(Status.Disabled(conditionEvaluationResult.reason.orEmpty()))
        }

        val throwableCollector = context.throwableCollector
        extensionManager.executeBeforeGroup(context)
        if (!throwableCollector.isEmpty()) {
            return createResultWithDocumentResultsSkipped(Status.Exception(throwableCollector.throwable))
        }

        val results = documentClasses
            .map { createDocumentFixture(it) }
            .map { executeDocument(it) }
        extensionManager.executeAfterGroup(context)
        if (!throwableCollector.isEmpty()) {
            return createResult(results, Status.Exception(throwableCollector.throwable))
        }

        return createResult(results, Status.Executed)
    }

    private fun createDocumentFixture(documentClass: KClass<*>): DocumentFixture {
        val documentFixtureEngineContext = DocumentFixture.createContext(documentClass, context)
        return DocumentFixture(documentFixtureEngineContext, repositoryManager, fixtureManager, extensionManager)
    }

    private fun executeDocument(documentFixture: DocumentFixture): DocumentResult {
        return documentFixture.execute()
    }

    private fun createResult(results: List<DocumentResult>, status: Status): GroupResult {
        return GroupResultImpl(results, status)
    }

    private fun createResultWithDocumentResultsSkipped(status: Status): GroupResult {
        val results = documentClasses.map {
            DocumentResult.Builder().withDocumentClass(it.java).withTags(getTags(it)).withStatus(Status.Skipped).build()
        }
        return createResult(results, status)
    }

    companion object {
        private val rootExtensionRegistry = ExtensionRegistryImpl.createRootExtensionRegistry()

        fun createContext(groupClass: KClass<*>): EngineContext {
            val extensionContext =
                GroupContextImpl(groupClass)
            val extensionRegistry = ExtensionRegistryImpl.createRegistryFrom(
                ExtensionRegistryImpl.loadExtensions(groupClass),
                rootExtensionRegistry
            )
            return EngineContext(null, extensionContext, extensionRegistry)
        }
    }
}
