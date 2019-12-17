package org.livingdoc.engine

import org.livingdoc.api.After
import org.livingdoc.api.Before
import java.lang.reflect.Method

internal class GroupFixtureModel(groupClass: Class<*>) {
    val beforeMethods: List<Method>
    val afterMethods: List<Method>

    init {
        val beforeMethods = mutableListOf<Method>()
        val afterMethods = mutableListOf<Method>()

        groupClass.declaredMethods.forEach { method ->
            if (method.isAnnotationPresent(Before::class.java)) beforeMethods.add(method)
            if (method.isAnnotationPresent(After::class.java)) afterMethods.add(method)
        }

        this.beforeMethods = beforeMethods.sortedBy { it.name }
        this.afterMethods = afterMethods.sortedBy { it.name }
    }
}
