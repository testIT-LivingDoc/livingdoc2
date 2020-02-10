package org.livingdoc.jvm.engine

import org.livingdoc.jvm.engine.extension.DocumentFixtureContextImpl
import org.livingdoc.jvm.engine.manager.ExtensionManager
import org.livingdoc.jvm.engine.manager.FixtureManager
import org.livingdoc.jvm.extension.GroupContext
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.results.documents.DocumentResult
import kotlin.reflect.KClass

internal class Group(
    private val context: GroupContext,
    private val documentClasses: List<KClass<*>>,
    private val repositoryManager: RepositoryManager,
    private val fixtureManager: FixtureManager,
    private val extensionManager: ExtensionManager
) {
    fun execute(): List<DocumentResult> {

        extensionManager.executeBeforeGroup(context)

        val results = documentClasses.map {
            val documentFixtureContext = DocumentFixtureContextImpl(it, context)
            val documentFixture =
                DocumentFixture(documentFixtureContext, repositoryManager, fixtureManager, extensionManager)
            documentFixture.execute()
        }
        extensionManager.executeAfterGroup(context)

        return results
    }
}
