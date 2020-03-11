package org.livingdoc.jvm.scenario

import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.jvm.api.fixture.Fixture
import org.livingdoc.jvm.api.fixture.FixtureManager
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
    private val manager: FixtureManager
) : Fixture<Scenario> {

    override fun execute(scenario: Scenario): TestDataResult<Scenario> {
        val srBuilder =
            ScenarioResult.Builder().withFixtureSource(fixtureModel.context.fixtureClass.java).withScenario(scenario)
        var successfulExecution = true
        if (shouldExecute(srBuilder)) {
            if (onBeforeScenarioCallback(srBuilder)) {
                val fixture = ScenarioFixtureInstance.createFixtureInstance(fixtureModel.context.fixtureClass)
                if (onBeforeScenarioMethod(fixture, srBuilder)) {
                    successfulExecution = executeSteps(scenario, fixture, srBuilder)
                }
                successfulExecution = successfulExecution and onAfterScenarioMethod(fixture, srBuilder)
            }
            successfulExecution = successfulExecution and onAfterScenarioCallback(srBuilder)
            if (successfulExecution) {
                srBuilder.withStatus(Status.Executed)
            }
        }

        return srBuilder.build()
    }

    /**
     * executes all steps in sequence until an exception occurs the resut is not executed
     */
    private fun executeSteps(
        scenario: Scenario,
        fixture: ScenarioFixtureInstance,
        srBuilder: ScenarioResult.Builder
    ): Boolean {
        var stopExecution = false
        val results = scenario.steps.map { step ->
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

        results.forEach {
            println(it)
            srBuilder.withStep(it)
        }
        return !stopExecution
    }

    /**
     * executes a single step
     */
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
                parameter to (matchingResult.variableToValue[parameterName]
                    ?: error("Missing parameter value: $parameterName"))
            }.toMap()

        try {
            ReflectionHelper.invokeWithParameterWithoutReturnValue(function, fixture, parameterList)
            stepResultBuilder.withStatus(Status.Executed)
        } catch (@Suppress("TooGenericExceptionCaught") t: Throwable) {
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

    private fun getParameterName(parameter: KParameter): String? {
        val parameterName = parameter.findAnnotation<Binding>()?.value
        return if (parameterName.isNullOrEmpty()) parameter.name else parameterName
    }

    private fun shouldExecute(srBuilder: ScenarioResult.Builder): Boolean {
        val shouldExecute = manager.shouldExecute()
        // TODO review usage of unassigned Skipped
        return if (shouldExecute.disabled) {
            srBuilder.withStatus(Status.Disabled(shouldExecute.reason.orEmpty())).withUnassignedSkipped()
            false
        } else {
            true
        }
    }

    private fun onBeforeScenarioCallback(srBuilder: ScenarioResult.Builder): Boolean {
        return try {
            manager.onBeforeFixture()
            true
        } catch (@Suppress("TooGenericExceptionCaught") throwable: Throwable) {
            val processedThrowable = manager.handleBeforeMethodExecutionException(throwable)
            if (processedThrowable != null) {
                srBuilder.withStatus(Status.Exception(processedThrowable)).withUnassignedSkipped()
                false
            } else {
                true
            }
        }
    }

    /**
     * execute before non static member functions annotated with @Before
     */
    private fun onBeforeScenarioMethod(fixture: ScenarioFixtureInstance, srBuilder: ScenarioResult.Builder): Boolean {
        var succeeded = true
        fixtureModel.beforeMethods.forEach {
            try {
                ReflectionHelper.invokeWithoutParameterWithoutReturnValue(it, fixture)
            } catch (@Suppress("TooGenericExceptionCaught") throwable: Throwable) {
                val processedThrowable = manager.handleBeforeMethodExecutionException(throwable)
                if (processedThrowable != null) {
                    srBuilder.withStatus(Status.Exception(processedThrowable)).withUnassignedSkipped()
                    succeeded = false
                }
            }
        }

        return succeeded
    }

    private fun onAfterScenarioMethod(fixture: ScenarioFixtureInstance, srBuilder: ScenarioResult.Builder): Boolean {
        var succeeded = true
        fixtureModel.afterMethods.forEach {
            try {
                ReflectionHelper.invokeWithoutParameterWithoutReturnValue(it, fixture)
            } catch (@Suppress("TooGenericExceptionCaught") throwable: Throwable) {
                val processedThrowable = manager.handleAfterMethodExecutionException(throwable)
                if (processedThrowable != null) {
                    srBuilder.withStatus(Status.Exception(processedThrowable)).withUnassignedSkipped()
                    succeeded = false
                }
            }
        }

        return succeeded
    }

    private fun onAfterScenarioCallback(srBuilder: ScenarioResult.Builder): Boolean {
        return try {
            manager.onAfterFixture()
            true
        } catch (@Suppress("TooGenericExceptionCaught") throwable: Throwable) {
            val processedThrowable = manager.handleAfterMethodExecutionException(throwable)
            if (processedThrowable != null) {
                srBuilder.withStatus(Status.Exception(processedThrowable)).withUnassignedSkipped()
                false
            } else {
                true
            }
        }
    }
}
