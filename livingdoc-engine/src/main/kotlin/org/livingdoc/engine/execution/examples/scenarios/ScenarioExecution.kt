package org.livingdoc.engine.execution.examples.scenarios

import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.engine.execution.Result
import org.livingdoc.engine.execution.examples.executeWithBeforeAndAfter
import org.livingdoc.engine.execution.examples.scenarios.matching.ScenarioFixtureChecker
import org.livingdoc.engine.execution.examples.scenarios.model.Scenario
import org.livingdoc.engine.execution.examples.scenarios.model.ScenarioResult
import org.livingdoc.engine.execution.examples.scenarios.model.StepResult
import org.livingdoc.engine.fixtures.FixtureMethodInvoker
import org.livingdoc.engine.fixtures.FixtureMethodInvoker.FixtureMethodInvocationException
import java.lang.reflect.Parameter

internal class ScenarioExecution(
        private val fixtureClass: Class<*>,
        scenario: Scenario,
        document: Any?
) {

    private val fixtureModel = ScenarioFixtureModel(fixtureClass)

    private val scenario = ScenarioResult.from(scenario)

    private val methodInvoker = FixtureMethodInvoker(document)

    /**
     * Executes the configured [Scenario].
     *
     * Does not throw any kind of exception. Exceptional state of the execution is packaged inside the [ScenarioResult] in
     * the form of different result objects.
     */
    fun execute(): ScenarioResult {
        try {
            assertFixtureIsDefinedCorrectly()
            executeScenario()
            markScenarioAsSuccessfullyExecuted()
        } catch (e: Throwable) {
            markScenarioAsExecutedWithException(e)
        }
        setSkippedStatusForAllUnknownResults()
        return scenario
    }

    private fun assertFixtureIsDefinedCorrectly() {
        val errors = ScenarioFixtureChecker.check(fixtureModel)
        if (errors.isNotEmpty()) {
            throw MalformedScenarioFixtureException(fixtureClass, errors)
        }
    }

    private fun executeScenario() {
        val fixture = createFixtureInstance()
        executeWithBeforeAndAfter(
                before = { invokeBeforeMethods(fixture) },
                body = { executeSteps(fixture) },
                after = { invokeAfterMethods(fixture) }
        )
    }

    private fun executeSteps(fixture: Any) {
        var previousResult: Result = Result.Executed
        for (step in scenario.steps) {
            if (previousResult == Result.Executed) {
                executeStep(fixture, step)
                previousResult = step.result
            } else {
                step.result = Result.Skipped
            }
        }
    }

    private fun executeStep(fixture: Any, step: StepResult) {
        val result = fixtureModel.getMatchingStepTemplate(step.value)
        val method = fixtureModel.getStepMethod(result.template)
        val parameterList = method.parameters
                .map {
                    result.variables.getOrElse(
                            getParameterName(it),
                            { error("Missing parameter value: ${getParameterName(it)}") })
                }
                .toTypedArray()
        step.result = invokeExpectingException {
            methodInvoker.invoke(method, fixture, parameterList)
        }
    }

    private fun getParameterName(parameter: Parameter): String {
        return parameter.getAnnotationsByType(Binding::class.java).firstOrNull()?.value
                ?: parameter.name
    }

    private fun invokeExpectingException(function: () -> Unit): Result {
        try {
            function.invoke()
            return Result.Executed
        } catch (e: AssertionError) {
            return Result.Failed(e)
        } catch (e: Exception) {
            return Result.Exception(e)
        }
    }

    private fun createFixtureInstance(): Any {
        return fixtureClass.newInstance()
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
            } catch (e: FixtureMethodInvocationException) {
                exceptions.add(e.cause!!)
            }
        }
        if (exceptions.isNotEmpty()) throw AfterMethodExecutionExeption(exceptions)
    }

    private fun markScenarioAsSuccessfullyExecuted() {
        scenario.result = Result.Executed
    }

    private fun markScenarioAsExecutedWithException(e: Throwable) {
        scenario.result = Result.Exception(e)
    }

    private fun setSkippedStatusForAllUnknownResults() {
        for (step in scenario.steps) {
            if (step.result is Result.Unknown) {
                step.result = Result.Skipped
            }
        }
    }

    internal class MalformedScenarioFixtureException(fixtureClass: Class<*>, errors: List<String>)
        : RuntimeException("The fixture class <$fixtureClass> is malformed: \n${errors.joinToString(separator = "\n", prefix = "  - ")}")

    internal class AfterMethodExecutionExeption(exceptions: List<Throwable>)
        : RuntimeException("One or more exceptions were thrown during execution of @After methods") {

        init {
            exceptions.forEach { addSuppressed(it) }
        }

    }

}
