package org.livingdoc.jvm.scenario

import org.livingdoc.jvm.api.extension.context.FixtureContext
import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureExtensionsInterface
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.results.Status
import org.livingdoc.results.TestDataResult
import org.livingdoc.results.examples.scenarios.ScenarioResult
import org.livingdoc.results.examples.scenarios.StepResult
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance

class ScenarioFixture(
    val context: FixtureContext,
    private val manager: FixtureExtensionsInterface
) : Fixture<Scenario> {

    private val fixtureModel = ScenarioFixtureModel(context.fixtureClass)

    override fun execute(scenario: Scenario): TestDataResult<Scenario> {
        val srBuilder = ScenarioResult.Builder().withFixtureSource(context.fixtureClass.java).withScenario(scenario)

        val shouldExecute = manager.shouldExecute()
        // unassigned Skipped sollte man sich nochmal ansehen
        if (shouldExecute.disabled) {
            srBuilder.withStatus(Status.Disabled(shouldExecute.reason.orEmpty())).withUnassignedSkipped()
        }

        try {
            manager.onBeforeFixture()

            val fixture = createFixtureInstance()

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

        return srBuilder.build()
    }

    private fun executeSteps(scenario: Scenario, fixture: Any): List<StepResult> {
        var previousStatus: Status = Status.Executed
        return scenario.steps.map { step ->
            val stepResultBuilder = StepResult.Builder().withValue(step.value)

            if (previousStatus == Status.Executed) {
                executeStep(fixture, step.value, stepResultBuilder)
            } else {
                stepResultBuilder.withStatus(Status.Skipped)
            }

            stepResultBuilder.build().also { previousStatus = it.status }
        }
    }

    private fun executeStep(fixture: Any, stepValue: String, stepResultBuilder: StepResult.Builder) {
        val result = fixtureModel.getMatchingStepTemplate(stepValue)
        val method = fixtureModel.getStepMethod(result.template)
        val parameterList = method.parameters
            .map { parameter ->
                result.variables.getOrElse(
                    getParameterName(parameter),
                    { error("Missing parameter value: ${getParameterName(parameter)}") })
            }
            .toTypedArray()
        stepResultBuilder.withStatus(
            invokeExpectingException(method, fixture, parameterList)
        ).withFixtureMethod(method)
    }

    private fun getParameterName(parameter: KParameter): String {
        //  return parameter.getAnnotationsByType(Binding::class.java).firstOrNull()?.value
        //      ?: parameter.name
        // noch nicht aequivalent
        return parameter.annotations.toString()
    }

    private fun invokeExpectingException(
        method: Method,
        fixture: Any,
        parameterList: Array<String>
    ): Status {
        return try {
            methodInvoker.invoke(method, fixture, parameterList)
            if (parameterList.contains(ExampleSyntax.EXCEPTION)) {
                return Status.Failed(NoExpectedExceptionThrownException())
            }
            Status.Executed
        } catch (e: AssertionError) {
            this.handleAssertionError(parameterList, e)
        } catch (e: FixtureMethodInvoker.ExpectedException) {
            Status.Executed
        } catch (e: Exception) {
            Status.Exception(e)
        }
    }

    /**
     * Creates a new instance of the fixture class passed to this execution
     */

    private fun createFixtureInstance(): Any {
        return context.fixtureClass.createInstance()
    }
}
