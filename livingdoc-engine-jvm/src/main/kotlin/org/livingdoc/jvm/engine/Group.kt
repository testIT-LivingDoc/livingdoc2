package org.livingdoc.jvm.engine

import org.livingdoc.jvm.engine.extension.DocumentFixtureContextImpl
import org.livingdoc.jvm.engine.manager.ExtensionManager
import org.livingdoc.jvm.engine.manager.FixtureManager
import org.livingdoc.jvm.api.extension.context.GroupContext
import org.livingdoc.repositories.RepositoryManager
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
        extensionManager.loadExtensions(context)

        if (!extensionManager.shouldExecute(context)) {
            return emptyList()
        }
        if (!context.throwableCollector.isEmpty()) {
            return emptyList()
        }

        extensionManager.executeBeforeGroup(context)
        if (!context.throwableCollector.isEmpty()) {
            return emptyList()
        }

        val results = documentClasses.map {
            val documentFixtureContext = DocumentFixtureContextImpl(it, context.extensionContext as GroupContext)
            val documentFixtureEngineContext = EngineContext(context, documentFixtureContext)
            val documentFixture =
                DocumentFixture(documentFixtureEngineContext, repositoryManager, fixtureManager, extensionManager)
            documentFixture.execute()
        }
        extensionManager.executeAfterGroup(context)
        if (!context.throwableCollector.isEmpty()) {
            return emptyList()
        }

        return results
    }
}
