package org.livingdoc.engine

import org.livingdoc.engine.execution.DocumentResult
import org.livingdoc.engine.fixtures.FixtureMethodInvoker
import org.livingdoc.repositories.RepositoryManager

internal class GroupExecution(
    groupClass: Class<*>,
    private val documentClasses: List<Class<*>>,
    private val repositoryManager: RepositoryManager,
    private val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher,
    private val scenarioToFixtureMatcher: ScenarioToFixtureMatcher
) {
    private val groupFixtureModel: GroupFixtureModel = GroupFixtureModel(groupClass)
    private val methodInvoker: FixtureMethodInvoker = FixtureMethodInvoker(groupClass)
    private val fixture: Any = groupClass.getDeclaredConstructor().newInstance()

    fun execute(): List<DocumentResult> {
        executeBeforeMethods()
        val results = executeDocuments()
        executeAfterMethods()

        return results
    }

    private fun executeBeforeMethods() {
        groupFixtureModel.beforeMethods.forEach { method -> methodInvoker.invoke(method, fixture) }
    }

    private fun executeDocuments(): List<DocumentResult> {
        return documentClasses.map { documentClass ->
            DocumentFixture(
                documentClass,
                repositoryManager,
                decisionTableToFixtureMatcher,
                scenarioToFixtureMatcher
            ).execute()
        }
    }

    private fun executeAfterMethods() {
        groupFixtureModel.afterMethods.forEach { method -> methodInvoker.invoke(method, fixture) }
    }
}
