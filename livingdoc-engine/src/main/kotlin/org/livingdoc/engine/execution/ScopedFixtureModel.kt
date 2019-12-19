package org.livingdoc.engine.execution

import org.livingdoc.api.After
import org.livingdoc.api.Before
import java.lang.reflect.Method

/**
 * ScopedFixtureModel represents a fixture that can have [Before] and [After] methods
 */
internal open class ScopedFixtureModel(
    private val fixtureClass: Class<*>
) {
    val beforeMethods: List<Method> = fixtureClass.declaredMethods.filter { method ->
        method.isAnnotationPresent(Before::class.java)
    }.sortedBy { method -> method.name }

    val afterMethods: List<Method> = fixtureClass.declaredMethods.filter { method ->
        method.isAnnotationPresent(After::class.java)
    }.sortedBy { method -> method.name }
}
