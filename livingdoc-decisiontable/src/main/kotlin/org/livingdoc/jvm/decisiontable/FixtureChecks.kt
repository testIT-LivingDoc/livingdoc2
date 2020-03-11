package org.livingdoc.jvm.decisiontable

import kotlin.reflect.KCallable

/**
 * This function tests a list of functions for their number of parameters that should be zero
 * @param methods The list of methods that should be checked
 * @param annotationClass the class where the methods are from
 * @return a list of messages with a message for every method that has more than zero parameters
 */
internal fun checkThatMethodsHaveNoParameters(methods: List<KCallable<*>>, annotationClass: Class<*>): List<String> {
    val annotationName = annotationClass.simpleName
    return methods
        .filter { it.parameters.isNotEmpty() }
        .map { "@$annotationName method <$it> has ${it.parameters.size} parameter(s) - must not have any!" }
}

/**
 * This function tests a list of functions for their number of parameters that should be one
 * @param methods The list of methods that should be checked
 * @param annotationClass the class where the methods are from
 * @return a list of messages with a message for every method that has more or less than one parameter
 */
internal fun checkThatMethodsHaveExactlyOneParameter(
    methods: List<KCallable<*>>,
    annotationClass: Class<*>
): List<String> {
    val annotationName = annotationClass.simpleName
    return methods
        .filter { it.parameters.size != 1 }
        .map { "@$annotationName method <$it> has ${it.parameters.size} parameter(s) - must have exactly 1!" }
}

/**
 * This function tests a list of functions for being static
 * @param methods The list of methods that should be checked
 * @param annotationClass the class where the methods are from
 * @return a list of messages with a message for every method that is not static
 */
internal fun checkThatMethodsAreStatic(methods: List<KCallable<*>>, annotationClass: Class<*>): List<String> {
    val annotationName = annotationClass.simpleName
    return methods
        .filter { !it.isOpen }
        .map { "@$annotationName method <$it> must be static!" }
}

/**
 * This function tests a list of functions for being non-static
 * @param methods The list of methods that should be checked
 * @param annotationClass the class where the methods are from
 * @return a list of messages with a message for every method that is static
 */
internal fun checkThatMethodsAreNonStatic(methods: List<KCallable<*>>, annotationClass: Class<*>): List<String> {
    val annotationName = annotationClass.simpleName
    return methods
        .filter { it.isOpen }
        .map { "@$annotationName method <$it> must not be static!" }
}
