package org.livingdoc.engine

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.documents.Group
import org.livingdoc.config.ConfigProvider
import org.livingdoc.engine.execution.DocumentResult
import org.livingdoc.engine.execution.ExecutionException
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
    val repositoryManager: RepositoryManager = RepositoryManager.from(RepositoryConfiguration.from(configProvider)),
    private val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher = DecisionTableToFixtureMatcher(),
    private val scenarioToFixtureMatcher: ScenarioToFixtureMatcher = ScenarioToFixtureMatcher()
) {
    @Throws(ExecutionException::class)
    fun execute(documentClasses: List<Class<*>>): List<DocumentResult> {
        return documentClasses.groupBy { documentClass ->
            documentClass.declaringClass?.takeIf { declaringClass ->
                declaringClass.getAnnotation(Group::class.java) != null
            }
        }.flatMap { (groupClass, documentClasses) ->
            when (groupClass) {
                null -> documentClasses.map { documentClass -> executeDocument(documentClass) }
                else -> executeGroup(groupClass, documentClasses)
            }
        }
    }

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

    @Throws(ExecutionException::class)
    private fun executeDocument(documentClass: Class<*>): DocumentResult {
        return DocumentFixture(documentClass, repositoryManager,
            decisionTableToFixtureMatcher, scenarioToFixtureMatcher).execute()
    }
}
