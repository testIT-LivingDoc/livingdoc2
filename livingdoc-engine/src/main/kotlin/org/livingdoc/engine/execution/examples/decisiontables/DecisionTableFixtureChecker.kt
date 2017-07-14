package org.livingdoc.engine.execution.examples.decisiontables

import org.livingdoc.api.fixtures.decisiontables.*
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

internal object DecisionTableFixtureChecker {

    fun check(model: DecisionTableFixtureModel): List<String> {
        return mutableListOf<String>().apply {

            addAll(elements = fixtureClassHasDefaultConstructor(model))

            addAll(elements = noAliasIsUsedTwice(model))

            addAll(elements = beforeTableMethodsHaveValidSignature(model))
            addAll(elements = afterTableMethodsHaveValidSignature(model))
            addAll(elements = beforeRowMethodsHaveValidSignature(model))
            addAll(elements = afterRowMethodsHaveValidSignature(model))
            addAll(elements = beforeFirstCheckMethodsHaveValidSignature(model))

            addAll(elements = inputMethodsHaveValidSignature(model))
            addAll(elements = checkMethodsHaveValidSignature(model))

        }
    }

    private fun fixtureClassHasDefaultConstructor(model: DecisionTableFixtureModel): Collection<String> {
        val fixtureClass = model.fixtureClass
        val defaultConstructors = fixtureClass.constructors
                .filter { it.parameterCount == 0 }
        if (defaultConstructors.isEmpty()) {
            return listOf("The fixture class <${fixtureClass.canonicalName}> has no default constructor!")
        }
        return emptyList()
    }

    private fun noAliasIsUsedTwice(model: DecisionTableFixtureModel): Collection<String> { // TODO: abstraction
        val errors = mutableListOf<String>()
        val knownAliases = mutableSetOf<String>()
        model.inputFields.forEach { field ->
            field.getAnnotationsByType(Input::class.java)
                    .flatMap { it.value.asIterable() }
                    .forEach { alias ->
                        if (knownAliases.contains(alias))
                            errors.add("Alias <$alias> is used multiple times!")
                        else knownAliases.add(alias)
                    }
        }
        model.inputMethods.forEach { method ->
            method.getAnnotationsByType(Input::class.java)
                    .flatMap { it.value.asIterable() }
                    .forEach { alias ->
                        if (knownAliases.contains(alias))
                            errors.add("Alias <$alias> is used multiple times!")
                        else knownAliases.add(alias)
                    }
        }
        model.checkMethods.forEach { method ->
            method.getAnnotationsByType(Check::class.java)
                    .flatMap { it.value.asIterable() }
                    .forEach { alias ->
                        if (knownAliases.contains(alias))
                            errors.add("Alias <$alias> is used multiple times!")
                        else knownAliases.add(alias)
                    }
        }
        return errors
    }

    private fun beforeTableMethodsHaveValidSignature(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveNoParameters(model.beforeTableMethods, BeforeTable::class))
        errors.addAll(elements = checkThatMethodsAreStatic(model.beforeTableMethods, BeforeTable::class))
        return errors
    }

    private fun afterTableMethodsHaveValidSignature(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveNoParameters(model.afterTableMethods, AfterTable::class))
        errors.addAll(elements = checkThatMethodsAreStatic(model.afterTableMethods, AfterTable::class))
        return errors
    }

    private fun beforeRowMethodsHaveValidSignature(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveNoParameters(model.beforeRowMethods, BeforeRow::class))
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.beforeRowMethods, BeforeRow::class))
        return errors
    }

    private fun afterRowMethodsHaveValidSignature(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveNoParameters(model.afterRowMethods, AfterRow::class))
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.afterRowMethods, AfterRow::class))
        return errors
    }

    private fun beforeFirstCheckMethodsHaveValidSignature(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveNoParameters(model.beforeFirstCheckMethods, BeforeFirstCheck::class))
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.beforeFirstCheckMethods, BeforeFirstCheck::class))
        return errors
    }

    private fun inputMethodsHaveValidSignature(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveExactlyOneParameter(model.inputMethods, Input::class))
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.inputMethods, Input::class))
        return errors
    }

    private fun checkMethodsHaveValidSignature(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveExactlyOneParameter(model.checkMethods, Check::class))
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.checkMethods, Check::class))
        return errors
    }

    private fun checkThatMethodsHaveNoParameters(methods: Collection<Method>, annotationClass: KClass<*>): Collection<String> {
        val annotationName = annotationClass.java.simpleName
        return methods
                .filter { it.parameterCount > 0 }
                .map { "@$annotationName method <$it> has ${it.parameterCount} parameter(s) - must not have any!" }
    }

    private fun checkThatMethodsHaveExactlyOneParameter(methods: Collection<Method>, annotationClass: KClass<*>): Collection<String> {
        val annotationName = annotationClass.java.simpleName
        return methods
                .filter { it.parameterCount != 1 }
                .map { "@$annotationName method <$it> has ${it.parameterCount} parameter(s) - must have exactly 1!" }
    }

    private fun checkThatMethodsAreStatic(methods: Collection<Method>, annotationClass: KClass<*>): Collection<String> {
        val annotationName = annotationClass.java.simpleName
        return methods
                .filter { !Modifier.isStatic(it.modifiers) }
                .map { "@$annotationName method <$it> must be static!" }
    }

    private fun checkThatMethodsAreNonStatic(methods: Collection<Method>, annotationClass: KClass<*>): Collection<String> {
        val annotationName = annotationClass.java.simpleName
        return methods
                .filter { Modifier.isStatic(it.modifiers) }
                .map { "@$annotationName method <$it> must not be static!" }
    }

}