package org.livingdoc.engine

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.documents.Group
import org.livingdoc.config.ConfigProvider
import org.livingdoc.engine.execution.ExecutionException
import org.livingdoc.engine.execution.documents.DocumentResult
import org.livingdoc.engine.execution.groups.GroupFixture
import org.livingdoc.engine.execution.groups.ImplicitGroup
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.repositories.config.RepositoryConfiguration

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
    private val repositoryManager: RepositoryManager =
        RepositoryManager.from(RepositoryConfiguration.from(configProvider)),
    private val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher = DecisionTableToFixtureMatcher(),
    private val scenarioToFixtureMatcher: ScenarioToFixtureMatcher = ScenarioToFixtureMatcher()
) {
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
        return documentClasses.groupBy { documentClass ->
            documentClass.declaringClass?.takeIf { declaringClass ->
                declaringClass.isAnnotationPresent(Group::class.java)
            } ?: ImplicitGroup::class.java
        }.flatMap { (groupClass, documentClasses) ->
            executeGroup(groupClass, documentClasses)
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
}
