package org.livingdoc.jvm.scenario

import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureExtensionsInterface
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.results.Status
import org.livingdoc.results.TestDataResult
import org.livingdoc.results.examples.scenarios.ScenarioResult
import org.livingdoc.results.examples.scenarios.StepResult
import org.livingdoc.scenario.matching.ScenarioStepMatcher
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaMethod

class ScenarioFixture(
    private val fixtureModel: ScenarioFixtureModel,
    private val manager: FixtureExtensionsInterface
) : Fixture<Scenario> {

    override fun execute(scenario: Scenario): TestDataResult<Scenario> {
        val srBuilder =
            ScenarioResult.Builder().withFixtureSource(fixtureModel.context.fixtureClass.java).withScenario(scenario)

        val shouldExecute = manager.shouldExecute()
        // unassigned Skipped sollte man sich nochmal ansehen
        if (shouldExecute.disabled) {
            srBuilder.withStatus(Status.Disabled(shouldExecute.reason.orEmpty())).withUnassignedSkipped()
        } else {
            try {
                manager.onBeforeFixture()

                val fixture = ScenarioFixtureInstance.createFixtureInstance(fixtureModel.context.fixtureClass)

                // TODO execute step
                executeSteps(scenario, fixture)

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
        }

        return srBuilder.build()
    }

    private fun executeSteps(scenario: Scenario, fixture: ScenarioFixtureInstance): List<StepResult> {
        var stopExecution = false
        return scenario.steps.map { step ->
            if (stopExecution) {
                StepResult.Builder().withValue(step.value).withStatus(Status.Skipped).build()
            } else {
                val result = executeStep(fixture, step.value)
                if (result.status !is Status.Executed) {
                    stopExecution = true
                }
                result
            }
        }
    }

    private fun executeStep(
        fixture: ScenarioFixtureInstance,
        stepValue: String
    ): StepResult {
        val stepResultBuilder = StepResult.Builder().withValue(stepValue)
        val matchingResult = getMatchingStepTemplate(stepValue)
        val function = fixtureModel.stepTemplateToMethod[matchingResult.template] ?: error("TODO")
        val parameterList = function.valueParameters
            .map { parameter ->
                val parameterName = getParameterName(parameter)
                parameter to (matchingResult.variableToValue[parameterName] ?: error("Missing parameter value: $parameterName"))
            }.toMap()

        try {
            ReflectionHelper.invokeWithParameterWithoutReturnValue(function, fixture, parameterList)
            stepResultBuilder.withStatus(Status.Executed)
        } catch (t: Throwable) {
            val throwable = manager.handleTestExecutionException(t)
            if (throwable != null) {
                stepResultBuilder.withStatus(Status.Exception(throwable))
            }
        }
        val method = function.javaMethod
        if (method != null) {
            stepResultBuilder.withFixtureMethod(method)
        }
        return stepResultBuilder.build()
    }

    private fun getMatchingStepTemplate(stepValue: String): ScenarioStepMatcher.MatchingResult {
        return ScenarioStepMatcher(fixtureModel.stepTemplates).match(stepValue)
    }

    private fun getParameterName(parameter: KParameter): String {
        return parameter.findAnnotation<Binding>()!!.value
    }
}
