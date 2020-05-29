package org.livingdoc.engine.execution.examples.scenarios

import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.api.fixtures.scenarios.Binding
import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.engine.execution.checkThatMethodsAreNonStatic
import org.livingdoc.engine.execution.checkThatMethodsHaveNoParameters
import org.livingdoc.engine.execution.examples.scenarios.matching.StepTemplate
import org.livingdoc.engine.execution.examples.scenarios.matching.Variable
import java.lang.reflect.Method

internal object ScenarioFixtureChecker {

    fun check(model: ScenarioFixtureModel): List<String> {
        return mutableListOf<String>().apply {
            addAll(elements = fixtureClassHasDefaultConstructor(model))

            addAll(elements = noAliasIsUsedTwice(model))

            addAll(elements = beforeScenarioMethodsHaveValidSignature(model))
            addAll(elements = afterScenarioMethodsHaveValidSignature(model))

            addAll(elements = stepMethodsHaveValidSignature(model))
        }
    }

    private fun fixtureClassHasDefaultConstructor(model: ScenarioFixtureModel): Collection<String> {
        val fixtureClass = model.fixtureClass
        val defaultConstructors = fixtureClass.constructors
            .filter { it.parameterCount == 0 }
        if (defaultConstructors.isEmpty()) {
            return listOf("The fixture class <${fixtureClass.canonicalName}> has no default constructor!")
        }
        return emptyList()
    }

    private fun noAliasIsUsedTwice(model: ScenarioFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        val knownAliases = mutableSetOf<String>()
        model.stepMethods.forEach { step ->
            step.getAnnotationsByType(Step::class.java)
                .flatMap { it.value.asIterable() }
                .forEach { alias ->
                    if (knownAliases.contains(alias))
                        errors.add("Alias <$alias> is used multiple times!")
                    else knownAliases.add(alias)
                }
        }
        return errors
    }

    private fun beforeScenarioMethodsHaveValidSignature(model: ScenarioFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveNoParameters(model.beforeMethods, Before::class.java))
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.beforeMethods, Before::class.java))
        return errors
    }

    private fun afterScenarioMethodsHaveValidSignature(model: ScenarioFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveNoParameters(model.afterMethods, After::class.java))
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.afterMethods, After::class.java))
        return errors
    }

    private fun stepMethodsHaveValidSignature(model: ScenarioFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.stepMethods, Step::class.java))
        errors.addAll(elements = checkThatStepTemplateMethodsHaveCorrectNumberOfParameters(model.stepTemplateToMethod))
        errors.addAll(elements = checkThatMethodParametersAreNamed(model.stepMethods))
        return errors
    }

    private fun checkThatStepTemplateMethodsHaveCorrectNumberOfParameters(
        stepTemplateToMethod: Map<StepTemplate, Method>
    ): Collection<String> {
        return stepTemplateToMethod
            .filter { (stepTemplate, method) ->
                stepTemplate.fragments.filterIsInstance<Variable>().count() != method.parameterCount
            }
            .map { (stepTemplate, method) ->
                "Method <$method> is annotated with a step template which has wrong parameter count: '$stepTemplate'"
            }
    }

    @Suppress("MaximumLineLength")
    private fun checkThatMethodParametersAreNamed(methods: Collection<Method>): Collection<String> {
        return methods
            .filter { method ->
                method.parameters.any {
                    !it.isNamePresent && !it.isAnnotationPresent(Binding::class.java)
                }
            }
            .map { method ->
                "Method <$method> has a parameter without a name! " +
                        "Either add @${Binding::class.java.simpleName} annotation or compile with '-parameters' flag"
            }
    }
}
