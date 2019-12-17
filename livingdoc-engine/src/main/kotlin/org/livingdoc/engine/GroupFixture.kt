package org.livingdoc.engine

import org.livingdoc.api.documents.Group
import org.livingdoc.engine.execution.DocumentResult
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

    val groupAnnotation: Group?
        get() = groupClass.getAnnotation(Group::class.java)

    private fun validate() {
        if (groupAnnotation == null) {
            throw IllegalArgumentException(
                "Group annotation is not present on class ${groupClass.canonicalName}."
            )
        }
    }
}
