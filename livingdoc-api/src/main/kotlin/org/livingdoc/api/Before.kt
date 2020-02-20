package org.livingdoc.api

/**
 * Methods annotated with this annotation are invoked before the first fixture of an [ExecutableDocument] or
 * before the first scenario [Step] method is invoked.
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
annotation class Before
