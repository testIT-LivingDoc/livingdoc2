package org.livingdoc.engine.execution.groups

import org.livingdoc.api.After
import org.livingdoc.api.Before
import org.livingdoc.engine.execution.checkThatMethodsAreStatic
import org.livingdoc.engine.execution.checkThatMethodsHaveNoParameters

/**
 * GroupFixtureChecker validates that a [GroupFixture] is defined correctly.
 *
 * @see GroupFixture
 * @see GroupFixtureModel
 */
internal object GroupFixtureChecker {
    /**
     * Check performs the actual validation on a [GroupFixtureModel].
     *
     * @param model the model of the [GroupFixture] to validate
     * @return a list of strings describing all validation errors
     * @see GroupFixtureModel
     */
    fun check(model: GroupFixtureModel): List<String> {
        return mutableListOf<String>().apply {
            addAll(checkBeforeMethodsHaveValidSignature(model))
            addAll(checkAfterMethodsHaveValidSignature(model))
        }
    }

    /**
     * checkBeforeMethodsHaveValidSignature ensures that all methods annotated with [Before] have a valid signature
     *
     * @param model the model of the [GroupFixture] to validate
     * @return a list of strings describing all validation errors
     */
    private fun checkBeforeMethodsHaveValidSignature(model: GroupFixtureModel): List<String> {
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
     * @param model the model of the [GroupFixture] to validate
     * @return a list of strings describing all validation errors
     */
    private fun checkAfterMethodsHaveValidSignature(model: GroupFixtureModel): List<String> {
        val errors = mutableListOf<String>()

        model.afterMethods.apply {
            errors.addAll(checkThatMethodsHaveNoParameters(this, After::class.java))
            errors.addAll(checkThatMethodsAreStatic(this, After::class.java))
        }

        return errors
    }
}
