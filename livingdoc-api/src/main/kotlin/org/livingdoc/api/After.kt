package org.livingdoc.api

/**
 * Methods annotated with this annotation are invoked after the last fixture of an [ExecutableDocument] or the
 * last scenario [Step] method was invoked.
 *
 *
 * **Constraints:**
 *
 *  1. The annotated method must not have any parameters!
 *  1. If multiple methods of a single fixture are annotated the invocation order is non-deterministic!
 *
 *
 * @see ExecutableDocument
 *
 * @see Step
 *
 * @see ScenarioFixture
 *
 * @since 2.0
 */
@Target(AnnotationTarget.FUNCTION)
annotation class After
