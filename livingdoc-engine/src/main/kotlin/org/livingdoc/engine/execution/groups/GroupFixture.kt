package org.livingdoc.engine.execution.groups

import org.livingdoc.api.documents.Group
import org.livingdoc.engine.DecisionTableToFixtureMatcher
import org.livingdoc.engine.ScenarioToFixtureMatcher
import org.livingdoc.engine.execution.documents.DocumentFixture
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.results.documents.DocumentResult

/**
 * A GroupFixture represents a class containing a number of [DocumentFixtures][DocumentFixture] which will be executed
 * together.
 *
 * @see DocumentFixture
 */
internal class GroupFixture(
    private val groupClass: Class<*>,
    private val documentClasses: List<Class<*>>,
    private val repositoryManager: RepositoryManager,
    private val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher,
    private val scenarioToFixtureMatcher: ScenarioToFixtureMatcher
) {
    /**
     * Execute runs the group of [DocumentFixtures][DocumentFixture] described by this GroupFixture.
     *
     * @return a list of [DocumentResults][DocumentResult], one for each [DocumentFixture]
     */
    fun execute(): List<DocumentResult> {
        validate()

        return GroupExecution(
            groupClass,
            documentClasses,
            repositoryManager,
            decisionTableToFixtureMatcher,
            scenarioToFixtureMatcher
        ).execute()
    }

    /**
     * Validate ensures that this instance represents a valid [GroupFixture].
     */
    private fun validate() {
        if (!groupClass.isAnnotationPresent(Group::class.java)) {
            throw IllegalArgumentException(
                "Group annotation is not present on class ${groupClass.canonicalName}."
            )
        }
    }
}
