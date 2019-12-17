package org.livingdoc.engine.execution.groups

import org.livingdoc.api.documents.Group
import org.livingdoc.engine.DecisionTableToFixtureMatcher
import org.livingdoc.engine.ScenarioToFixtureMatcher
import org.livingdoc.engine.execution.documents.DocumentResult
import org.livingdoc.repositories.RepositoryManager

internal class GroupFixture(
    private val groupClass: Class<*>,
    private val documentClasses: List<Class<*>>,
    private val repositoryManager: RepositoryManager,
    private val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher,
    private val scenarioToFixtureMatcher: ScenarioToFixtureMatcher
) {
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

    private val groupAnnotation: Group?
        get() = groupClass.getAnnotation(Group::class.java)

    private fun validate() {
        if (groupAnnotation == null) {
            throw IllegalArgumentException(
                "Group annotation is not present on class ${groupClass.canonicalName}."
            )
        }
    }
}
