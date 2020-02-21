package org.livingdoc.engine

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.documents.Group
import org.livingdoc.api.tagging.Tag
import org.livingdoc.config.ConfigProvider
import org.livingdoc.engine.config.TaggingConfig
import org.livingdoc.engine.execution.ExecutionException
import org.livingdoc.engine.execution.MalformedFixtureException
import org.livingdoc.engine.execution.groups.GroupFixture
import org.livingdoc.engine.execution.groups.ImplicitGroup
import org.livingdoc.reports.ReportsManager
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.repositories.config.RepositoryConfiguration
import org.livingdoc.results.documents.DocumentResult
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Executes the given document class and returns the [DocumentResult]. The document's class must be annotated
 * with [ExecutableDocument].
 *
 * @return the [DocumentResult] of the execution
 * @throws ExecutionException in case the execution failed in a way that did not produce a viable result
 * @since 2.0
 */
class LivingDoc(
    private val configProvider: ConfigProvider = ConfigProvider.load(),
    private val repositoryManager: RepositoryManager =
        RepositoryManager.from(RepositoryConfiguration.from(configProvider)),
    private val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher = DecisionTableToFixtureMatcher(),
    private val scenarioToFixtureMatcher: ScenarioToFixtureMatcher = ScenarioToFixtureMatcher()
) {
    companion object {
        /**
         * Indicates whether the execution should fail fast due to a specific
         * thrown exception and not execute any more tests.
         */
        var failFastActivated: Boolean = false

        /**
         * Global ExecutorService which uses a WorkStealing(Thread)Pool
         * for the parallel execution of tasks in the LivingDoc engine
         */
        val executor: ExecutorService = Executors.newWorkStealingPool()
    }

    val taggingConfig = TaggingConfig.from(configProvider)
    /**
     * Executes the given document classes and returns the list of [DocumentResults][DocumentResult]. The document
     * classes must be annotated with [ExecutableDocument].
     *
     * @param documentClasses the document classes to execute
     * @return a list of [DocumentResults][DocumentResult] of the execution
     * @throws ExecutionException in case the execution failed in a way that did not produce a viable result
     */
    @Throws(ExecutionException::class)
    fun execute(documentClasses: List<Class<*>>): List<DocumentResult> {
        // Execute documents
        val documentResults = documentClasses.filter {
            val tags = getTags(it)
            when {
                taggingConfig.includedTags.isNotEmpty() && tags.none { tag -> taggingConfig.includedTags.contains(tag) }
                    -> false
                tags.any { tag -> taggingConfig.excludedTags.contains(tag) } -> false
                else -> true
            }
        }.groupBy { documentClass ->
            extractGroup(documentClass)
        }.flatMap { (groupClass, documentClasses) ->
            executeGroup(groupClass, documentClasses)
        }

        // Generate reports
        val reportsManager = ReportsManager.from(configProvider)
        reportsManager.generateReports(documentResults)

        // Return results for further processing
        return documentResults
    }

    private fun getTags(documentClass: Class<*>): List<String> {
        return documentClass.getAnnotationsByType(Tag::class.java).map {
            it.value
        }
    }

    /**
     * Executes the given group, which contains the given document classes and returns the list of
     * [DocumentResults][DocumentResult]. The group class must be annotated with [Group] and the document classes must
     * be annotated with [ExecutableDocument].
     *
     * @param groupClass the group that contains the documentClasses
     * @param documentClasses the document classes to execute
     * @return a list of [DocumentResults][DocumentResult] of the execution
     * @throws ExecutionException in case the execution failed in a way that did not produce a viable result
     */
    @Throws(ExecutionException::class)
    private fun executeGroup(groupClass: Class<*>, documentClasses: List<Class<*>>): List<DocumentResult> {
        return GroupFixture(
            groupClass,
            documentClasses,
            repositoryManager,
            decisionTableToFixtureMatcher,
            scenarioToFixtureMatcher
        ).execute()
    }

    private fun extractGroup(documentClass: Class<*>): Class<*> {
        val declaringGroup = documentClass.declaringClass?.takeIf { declaringClass ->
            declaringClass.isAnnotationPresent(Group::class.java)
        }

        val annotationGroup =
            documentClass.getAnnotation(ExecutableDocument::class.java).group.java.takeIf { annotationClass ->
                annotationClass.isAnnotationPresent(Group::class.java)
            }

        if (declaringGroup != null && annotationGroup != null && declaringGroup != annotationGroup)
            throw MalformedFixtureException(
                documentClass, listOf(
                    "Ambiguous group definition: declared inside ${declaringGroup.name}, " +
                            "annotation specifies ${annotationGroup.name}"
                )
            )

        return declaringGroup ?: annotationGroup ?: ImplicitGroup::class.java
    }
}
