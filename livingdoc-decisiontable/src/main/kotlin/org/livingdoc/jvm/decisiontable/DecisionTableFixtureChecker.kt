package org.livingdoc.jvm.decisiontable

import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.api.fixtures.decisiontables.AfterRow
import org.livingdoc.api.fixtures.decisiontables.BeforeFirstCheck
import org.livingdoc.api.fixtures.decisiontables.BeforeRow
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.Input
import kotlin.reflect.KAnnotatedElement
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
            .filter { it.parameters.isEmpty() }
        if (defaultConstructors.isEmpty()) {
            return listOf("The fixture class <${fixtureClass.qualifiedName}> has no default constructor!")
        }
        return emptyList()
    }

    private fun noAliasIsUsedTwice(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        val knownAliases = mutableSetOf<String>()

        val handleAlias: (String) -> Unit = { alias ->
            if (knownAliases.contains(alias))
                errors.add("Alias <$alias> is used multiple times!")
            else knownAliases.add(alias)
        }

        checkAliasesOf(model.inputFields, Input::class, { it.value }, handleAlias)
        checkAliasesOf(model.inputMethods, Input::class, { it.value }, handleAlias)
        checkAliasesOf(model.checkMethods, Check::class, { it.value }, handleAlias)

        return errors
    }

    private fun <T : Annotation> checkAliasesOf(
        elements: Iterable<KAnnotatedElement>,
        annotation: KClass<T>,
        flatMapper: (T) -> String,
        handler: (String) -> Unit
    ) {
        elements.forEach { annotatedElement ->
            annotatedElement.annotations
                .filterIsInstance(annotation.java)
                .map { flatMapper.invoke(it) }
                .forEach { handler.invoke(it) }
        }
    }

    private fun beforeTableMethodsHaveValidSignature(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveNoParameters(model.beforeMethods, Before::class.java))
        errors.addAll(elements = checkThatMethodsAreStatic(model.beforeMethods, Before::class.java))
        return errors
    }

    private fun afterTableMethodsHaveValidSignature(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveNoParameters(model.afterMethods, After::class.java))
        errors.addAll(elements = checkThatMethodsAreStatic(model.afterMethods, After::class.java))
        return errors
    }

    private fun beforeRowMethodsHaveValidSignature(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveNoParameters(model.beforeRowMethods, BeforeRow::class.java))
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.beforeRowMethods, BeforeRow::class.java))
        return errors
    }

    private fun afterRowMethodsHaveValidSignature(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveNoParameters(model.afterRowMethods, AfterRow::class.java))
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.afterRowMethods, AfterRow::class.java))
        return errors
    }

    private fun beforeFirstCheckMethodsHaveValidSignature(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(
            elements = checkThatMethodsHaveNoParameters(
                model.beforeFirstCheckMethods,
                BeforeFirstCheck::class.java
            )
        )
        errors.addAll(checkThatMethodsAreNonStatic(model.beforeFirstCheckMethods, BeforeFirstCheck::class.java))
        return errors
    }

    private fun inputMethodsHaveValidSignature(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveExactlyOneParameter(model.inputMethods, Input::class.java))
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.inputMethods, Input::class.java))
        return errors
    }

    private fun checkMethodsHaveValidSignature(model: DecisionTableFixtureModel): Collection<String> {
        val errors = mutableListOf<String>()
        errors.addAll(elements = checkThatMethodsHaveExactlyOneParameter(model.checkMethods, Check::class.java))
        errors.addAll(elements = checkThatMethodsAreNonStatic(model.checkMethods, Check::class.java))
        return errors
    }
}
