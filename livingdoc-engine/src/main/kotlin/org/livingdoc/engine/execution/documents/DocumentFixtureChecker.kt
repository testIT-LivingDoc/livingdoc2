package org.livingdoc.engine.execution.documents

import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.engine.execution.checkThatMethodsAreStatic
import org.livingdoc.engine.execution.checkThatMethodsHaveNoParameters

/**
 * DocumentFixtureChecker validates that a [DocumentFixture] is defined correctly.
 *
 * @see DocumentFixture
 * @see DocumentFixtureModel
 */
internal object DocumentFixtureChecker {
    /**
     * Check performs the actual validation on a [DocumentFixtureModel].
     *
     * @param model the model of the [DocumentFixture] to validate
     * @return a list of strings describing all validation errors
     * @see DocumentFixtureModel
     */
    fun check(model: DocumentFixtureModel): List<String> {
        return mutableListOf<String>().apply {
            addAll(checkBeforeMethodsHaveValidSignature(model))
            addAll(checkAfterMethodsHaveValidSignature(model))
        }
    }

    /**
     * checkBeforeMethodsHaveValidSignature ensures that all methods annotated with [Before] have a valid signature
     *
     * @param model the model of the [DocumentFixture] to validate
     * @return a list of strings describing all validation errors
     */
    private fun checkBeforeMethodsHaveValidSignature(model: DocumentFixtureModel): List<String> {
        val errors = mutableListOf<String>()

        model.beforeMethods.apply {
            errors.addAll(checkThatMethodsHaveNoParameters(this, Before::class.java))
            errors.addAll(checkThatMethodsAreStatic(this, Before::class.java))
        }

        return errors
    }

    /**
     * checkAfterMethodsHaveValidSignature ensures that all methods annotated with [Before] have a valid signature
     *
     * @param model the model of the [DocumentFixture] to validate
     * @return a list of strings describing all validation errors
     */
    private fun checkAfterMethodsHaveValidSignature(model: DocumentFixtureModel): List<String> {
        val errors = mutableListOf<String>()

        model.afterMethods.apply {
            errors.addAll(checkThatMethodsHaveNoParameters(this, After::class.java))
            errors.addAll(checkThatMethodsAreStatic(this, After::class.java))
        }

        return errors
    }
}
