package org.livingdoc.engine.execution.examples.scenarios.matching

import org.livingdoc.api.fixtures.scenarios.After
import org.livingdoc.api.fixtures.scenarios.Before
import org.livingdoc.api.fixtures.scenarios.Step
import org.livingdoc.engine.execution.examples.scenarios.ScenarioFixtureModel
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass


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
        errors.addAll(elements = checkThatMethodsHaveNoParameters(model.beforeMethods, Before::class))
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.beforeMethods, Before::class))
        return errors
    }

    private fun afterScenarioMethodsHaveValidSignature(model: ScenarioFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveNoParameters(model.afterMethods, After::class))
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.afterMethods, After::class))
        return errors
    }

    private fun stepMethodsHaveValidSignature(model: ScenarioFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.stepMethods, Step::class))
        model.stepTemplateToMethod.forEach { stepTemplate, method ->
            if (stepTemplate.fragments.filter { it is Variable }.count() != method.parameterCount) {
                errors.add("Method <${method}> is annotated with step template with wrong parameter count: '${stepTemplate}'")
            }
        }
        return errors
    }

    private fun checkThatMethodsHaveNoParameters(methods: Collection<Method>, annotationClass: KClass<*>): Collection<String> {
        val annotationName = annotationClass.java.simpleName
        return methods
                .filter { it.parameterCount > 0 }
                .map { "@$annotationName method <$it> has ${it.parameterCount} parameter(s) - must not have any!" }
    }

    private fun checkThatMethodsAreNonStatic(methods: Collection<Method>, annotationClass: KClass<*>): Collection<String> {
        val annotationName = annotationClass.java.simpleName
        return methods
                .filter { Modifier.isStatic(it.modifiers) }
                .map { "@$annotationName method <$it> must not be static!" }
    }

}