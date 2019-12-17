package org.livingdoc.engine

import org.livingdoc.api.After
import org.livingdoc.api.Before
import java.lang.reflect.Method

internal open class ScopedFixtureModel(
    private val fixtureClass: Class<*>
) {
    val beforeMethods: List<Method>
        get() = fixtureClass.methods.filter { method -> method.isAnnotationPresent(Before::class.java) }
    val afterMethods: List<Method>
        get() = fixtureClass.methods.filter { method -> method.isAnnotationPresent(After::class.java) }
}
