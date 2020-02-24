package org.livingdoc.jvm.scenario

import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureExtensionsInterface
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.results.Status
import org.livingdoc.results.TestDataResult
import org.livingdoc.results.examples.scenarios.ScenarioResult

class ScenarioFixture(
    val context: FixtureContext,
    private val manager: FixtureExtensionsInterface
) : Fixture<Scenario> {
    override fun execute(testData: Scenario): TestDataResult<Scenario> {
        val srBuilder = ScenarioResult.Builder().withFixtureSource(context.fixtureClass.java).withScenario(testData)


        val shouldExecute = manager.shouldExecute()
        // unassigned Skipped sollte man sich nochmal ansehen
        if (shouldExecute.disabled) {
            srBuilder.withStatus(Status.Disabled(shouldExecute.reason.orEmpty())).withUnassignedSkipped()
        }


        try {
            manager.onBeforeFixture()


            //TODO execute step



            manager.onAfterFixture()

            srBuilder.withStatus(Status.Executed).withUnassignedSkipped()

            // laut Leon macht eine IllegalStateException an der Stelle keinen Sinn, daher brauchen wir das Failed
            // auch nicht. Daher waere ein throwable auch besser
        } catch (throwable: Throwable) {
            val processedThrowable = manager.handleTestExecutionException(throwable)
            if (processedThrowable != null) {
                srBuilder.withStatus(Status.Exception(processedThrowable)).withUnassignedSkipped().build()
            }

        }

        return srBuilder.build()
    }
}
