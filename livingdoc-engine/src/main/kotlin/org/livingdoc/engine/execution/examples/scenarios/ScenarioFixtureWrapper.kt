package org.livingdoc.engine.execution.examples.scenarios

import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.exception.ExampleSyntax
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.engine.LivingDoc
import org.livingdoc.engine.execution.examples.NoExpectedExceptionThrownException
import org.livingdoc.engine.execution.examples.executeWithBeforeAndAfter
import org.livingdoc.engine.fixtures.Fixture
import org.livingdoc.engine.fixtures.FixtureMethodInvoker
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.results.Status
import org.livingdoc.results.examples.scenarios.ScenarioResult
import org.livingdoc.results.examples.scenarios.StepResult
import java.lang.reflect.Method
import java.lang.reflect.Parameter

/**
 * Obviously wraps a scenario fixture
 */
class ScenarioFixtureWrapper(
    val fixtureClass: Class<*>
) : Fixture<Scenario> {

    private val fixtureModel = ScenarioFixtureModel(fixtureClass)

    private val methodInvoker = FixtureMethodInvoker(null)

    /**
     * Executes the wrapped fixture class with the give scenario. Does not throw any kind of exception. Exceptional
     * state of the execution is packaged inside the [ScenarioResult] in the form of different status objects.
     *
     * @param testData A scenario instance that can be mapped to the wrapped fixture
     * @return A ScenarioResult for the execution
     */
    override fun execute(testData: Scenario): ScenarioResult {
        val scenarioResultBuilder =
            ScenarioResult.Builder().withScenario(testData).withFixtureSource(fixtureClass)

        when {
            LivingDoc.failFastActivated -> {
                scenarioResultBuilder.withUnassignedSkipped()
            }
            fixtureClass.isAnnotationPresent(Disabled::class.java) -> {
                scenarioResultBuilder.withStatus(
                    Status.Disabled(fixtureClass.getAnnotation(Disabled::class.java).value)
                )
            }
            else -> {
                try {
                    assertFixtureIsDefinedCorrectly()
                    executeScenario(testData).forEach { scenarioResultBuilder.withStep(it) }
                    scenarioResultBuilder.withStatus(Status.Executed)
                } catch (e: Exception) {
                    scenarioResultBuilder.withStatus(Status.Exception(e))
                        .withUnassignedSkipped()
                } catch (e: AssertionError) {
                    scenarioResultBuilder.withStatus(Status.Exception(e))
                        .withUnassignedSkipped()
                }
            }
        }

        return scenarioResultBuilder.build()
    }

    private fun assertFixtureIsDefinedCorrectly() {
        val errors = ScenarioFixtureChecker.check(fixtureModel)
        if (errors.isNotEmpty()) {
            throw MalformedScenarioFixtureException(fixtureClass, errors)
        }
    }

    private fun executeScenario(scenario: Scenario): List<StepResult> {
        val fixture = createFixtureInstance()
        return executeWithBeforeAndAfter(
            before = { invokeBeforeMethods(fixture) },
            body = { executeSteps(scenario, fixture) },
            after = { invokeAfterMethods(fixture) }
        )
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

    private fun getParameterName(parameter: Parameter): String {
        return parameter.getAnnotationsByType(Binding::class.java).firstOrNull()?.value
            ?: parameter.name
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
        return fixtureClass.getDeclaredConstructor().newInstance()
    }

    private fun invokeBeforeMethods(fixture: Any) {
        fixtureModel.beforeMethods.forEach { methodInvoker.invoke(it, fixture) }
    }

    private fun invokeAfterMethods(fixture: Any) {
        val exceptions = mutableListOf<Throwable>()
        for (afterMethod in fixtureModel.afterMethods) {
            try {
                methodInvoker.invoke(afterMethod, fixture)
            } catch (e: AssertionError) {
                exceptions.add(e)
            } catch (e: FixtureMethodInvoker.FixtureMethodInvocationException) {
                exceptions.add(e.cause)
            }
        }
        if (exceptions.isNotEmpty()) throw AfterMethodExecutionException(exceptions)
    }

    private fun handleAssertionError(parameterList: Array<String>, e: AssertionError): Status {
        if (parameterList.contains(ExampleSyntax.EXCEPTION)) {
            return Status.Failed(NoExpectedExceptionThrownException())
        }
        return Status.Failed(e)
    }

    internal class MalformedScenarioFixtureException(fixtureClass: Class<*>, errors: List<String>) : RuntimeException(
        "The fixture class <$fixtureClass> is malformed: \n${errors.joinToString(
            separator = "\n",
            prefix = "  - "
        )}"
    )

    internal class AfterMethodExecutionException(exceptions: List<Throwable>) :
        RuntimeException("One or more exceptions were thrown during execution of @After methods") {

        init {
            exceptions.forEach { addSuppressed(it) }
        }
    }
}
