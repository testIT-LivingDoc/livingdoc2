package org.livingdoc.engine.execution

import java.lang.reflect.Method
import java.lang.reflect.Modifier

internal fun checkThatMethodsHaveNoParameters(methods: List<Method>, annotationClass: Class<*>): List<String> {
    val annotationName = annotationClass.simpleName
    return methods
        .filter { it.parameterCount > 0 }
        .map { "@$annotationName method <$it> has ${it.parameterCount} parameter(s) - must not have any!" }
}

internal fun checkThatMethodsHaveExactlyOneParameter(methods: List<Method>, annotationClass: Class<*>): List<String> {
    val annotationName = annotationClass.simpleName
    return methods
        .filter { it.parameterCount != 1 }
        .map { "@$annotationName method <$it> has ${it.parameterCount} parameter(s) - must have exactly 1!" }
}

internal fun checkThatMethodsAreStatic(methods: List<Method>, annotationClass: Class<*>): List<String> {
    val annotationName = annotationClass.simpleName
    return methods
        .filter { !Modifier.isStatic(it.modifiers) }
        .map { "@$annotationName method <$it> must be static!" }
}

internal fun checkThatMethodsAreNonStatic(methods: List<Method>, annotationClass: Class<*>): List<String> {
    val annotationName = annotationClass.simpleName
    return methods
        .filter { Modifier.isStatic(it.modifiers) }
        .map { "@$annotationName method <$it> must not be static!" }
}
