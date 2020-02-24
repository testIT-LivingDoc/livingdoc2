package org.livingdoc.jvm.engine

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.config.ConfigProvider
import org.livingdoc.engine.ExecutionException
import org.livingdoc.jvm.engine.config.TaggingConfig
import org.livingdoc.jvm.engine.config.filterTags
import org.livingdoc.jvm.engine.manager.ExtensionManager
import org.livingdoc.jvm.engine.manager.FixtureManager
import org.livingdoc.reports.ReportsManager
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.repositories.config.RepositoryConfiguration
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.groups.GroupResult
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@ExperimentalStdlibApi
class LivingDoc internal constructor(
    private val configProvider: ConfigProvider = ConfigProvider.load(),
    private val repositoryManager: RepositoryManager =
        RepositoryManager.from(RepositoryConfiguration.from(configProvider)),
    private val fixtureManager: FixtureManager = FixtureManager(),
    private val extensionManager: ExtensionManager = ExtensionManager()
) {

    private val taggingConfig = TaggingConfig.from(configProvider)

    /**
     * Executes the given document classes and returns the list of [DocumentResults][DocumentResult]. The document
     * classes must be annotated with [ExecutableDocument].
     *
     * @param documentClasses the document classes to execute
     * @return a list of [DocumentResults][DocumentResult] of the execution
     * @throws ExecutionException in case the execution failed in a way that did not produce a viable result
     */
    @Suppress("TooGenericExceptionCaught")
    @Throws(ExecutionException::class)
    fun execute(documentClasses: List<KClass<*>>): List<GroupResult> {
        try {
            val groupResults = documentClasses
                .filterTags(taggingConfig.includedTags, taggingConfig.excludedTags)
                .groupBy { extractGroup(it) }
                .map { (groupClass, documentClasses) -> createGroup(groupClass, documentClasses) }
                .map { executeGroup(it) }

            val reportsManager = ReportsManager.from(configProvider)
            reportsManager.generateReports(groupResults.flatMap { it.documentResults }) // TODO refactor reports

            return groupResults
        } catch (e: Throwable) {
            throw ExecutionException("Unhandled Exception was thrown", e)
        }
    }

    /**
     * Executes the given group, which contains the given document classes and returns the list of
     * [DocumentResults][DocumentResult]. The group class must be annotated with [Group] and the document classes must
     * be annotated with [ExecutableDocument].
     *
     * @param group the group that contains the documentClasses
     * @return a list of [DocumentResults][DocumentResult] of the execution
     * @throws ExecutionException in case the execution failed in a way that did not produce a viable result
     */
    @Throws(ExecutionException::class)
    private fun executeGroup(group: Group): GroupResult {
        return group.execute()
    }

    /**
     * Create a group, which contains the given document classes. The group class must be annotated with [Group] and the
     * document classes must be annotated with [ExecutableDocument].
     *
     * @param groupClass the group that contains the documentClasses
     * @param documentClasses the document classes to execute
     * @return a Group
     */
    private fun createGroup(groupClass: KClass<*>, documentClasses: List<KClass<*>>): Group {
        val context = Group.createContext(groupClass)

        return Group(
            context,
            documentClasses,
            repositoryManager,
            fixtureManager,
            extensionManager
        )
    }

    private fun extractGroup(documentClass: KClass<*>): KClass<*> {
        val declaringGroup = documentClass.outer?.takeIf { declaringClass ->
            declaringClass.hasAnnotation<org.livingdoc.api.documents.Group>()
        }

        val annotationGroup = documentClass.findAnnotation<ExecutableDocument>()!!.group.takeIf { annotationClass ->
            annotationClass.hasAnnotation<org.livingdoc.api.documents.Group>()
        }

        if (declaringGroup != null && annotationGroup != null && declaringGroup != annotationGroup) {
            throw IllegalArgumentException(
                "Ambiguous group definition for document class ${documentClass.qualifiedName}: declared inside " +
                        "${declaringGroup.qualifiedName}, annotation specifies ${annotationGroup.qualifiedName}"
            )
        }
        return declaringGroup ?: annotationGroup ?: ImplicitGroup::class
    }

    companion object {
        fun create() = LivingDoc()
    }
}
