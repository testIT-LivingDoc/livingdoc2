package org.livingdoc.api.fixtures.scenarios

/**
 * Binds a name to the annotated parameter. If the bound name occurs as parameter in a [Step] annotation of this method,
 * the extracted value will be converted and passed to this method during execution.
 *
 * @see Step
 * @see ScenarioFixture
 *
 * @since 2.0
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER
)
annotation class Binding(val value: String)
