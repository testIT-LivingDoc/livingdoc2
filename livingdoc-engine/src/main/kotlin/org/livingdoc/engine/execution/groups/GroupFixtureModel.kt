package org.livingdoc.engine.execution.groups

import org.livingdoc.api.documents.FailFast
import org.livingdoc.engine.execution.ScopedFixtureModel

/**
 * A GroupFixtureModel is a representation of the glue code necessary to execute a [GroupFixture]
 *
 * @see GroupFixture
 * @see GroupExecution
 */
internal class GroupFixtureModel(val groupClass: Class<*>) : ScopedFixtureModel(groupClass) {

    /**
     * getFailFastExceptions returns the specified exceptions for fail fast if the annotation is set.
     * Otherwise it returns an empty list.
     *
     * @return a list of Exception classes that should be considered for fail fast
     */
    val failFastExceptions: List<Class<*>>
    get() {
        return groupClass.getAnnotation(FailFast::class.java)?.onExceptionTypes?.map { it.java } ?: emptyList()
    }
}
