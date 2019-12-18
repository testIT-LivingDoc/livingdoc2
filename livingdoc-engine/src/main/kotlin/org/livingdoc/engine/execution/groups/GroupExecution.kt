package org.livingdoc.engine.execution.groups

import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.engine.DecisionTableToFixtureMatcher
import org.livingdoc.engine.ScenarioToFixtureMatcher
import org.livingdoc.engine.execution.MalformedFixtureException
import org.livingdoc.engine.execution.documents.DocumentFixture
import org.livingdoc.engine.execution.documents.DocumentResult
import org.livingdoc.engine.fixtures.FixtureMethodInvoker
import org.livingdoc.repositories.RepositoryManager

/**
 * A GroupExecution represents a single execution of a [GroupFixture].
 *
 * @see GroupFixture
 */
internal class GroupExecution(
    private val groupClass: Class<*>,
    private val documentClasses: List<Class<*>>,
    private val repositoryManager: RepositoryManager,
    private val decisionTableToFixtureMatcher: DecisionTableToFixtureMatcher,
    private val scenarioToFixtureMatcher: ScenarioToFixtureMatcher
) {
    private val groupFixtureModel: GroupFixtureModel = GroupFixtureModel(groupClass)
    private val methodInvoker: FixtureMethodInvoker = FixtureMethodInvoker(groupClass)
    private val fixture: Any = groupClass.getDeclaredConstructor().newInstance()

    /**
     * Executes performs the actual execution of the [GroupFixture]
     *
     * @return a list of [DocumentResults][DocumentResult], one for each [DocumentFixture] in the [GroupFixture].
     */
    fun execute(): List<DocumentResult> {
        assertFixtureIsDefinedCorrectly()
        executeBeforeMethods()
        val results = executeDocuments()
        executeAfterMethods()

        return results
    }

    /**
     * assertFixtureIsDefinedCorrectly checks that the [GroupFixture] is defined correctly
     */
    private fun assertFixtureIsDefinedCorrectly() {
        val errors = GroupFixtureChecker.check(groupFixtureModel)

        if (errors.isNotEmpty()) {
            throw MalformedFixtureException(groupClass, errors)
        }
    }

    /**
     * ExecuteBeforeMethods invokes all [Before] methods on the [GroupFixture].
     *
     * @see Before
     * @see GroupFixture
     */
    private fun executeBeforeMethods() {
        groupFixtureModel.beforeMethods.forEach { method -> methodInvoker.invoke(method, fixture) }
    }

    /**
     * ExecuteDocuments runs all [DocumentFixtures][DocumentFixture] containde in the group.
     *
     * @return a list of [DocumentResults][DocumentResult], one for each [DocumentFixture] in the [GroupFixture].
     */
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

    /**
     * ExecuteBeforeMethods invokes all [After] methods on the [GroupFixture].
     *
     * @see After
     * @see GroupFixture
     */
    private fun executeAfterMethods() {
        groupFixtureModel.afterMethods.forEach { method -> methodInvoker.invoke(method, fixture) }
    }
}
