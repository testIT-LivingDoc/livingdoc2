package org.livingdoc.api.fixtures.decisiontables

/**
 * Methods annotated with this annotation are invoked once for each row of the decision table example just before it is
 * evaluated. These methods are intended to setup resources needed for the execution of a single row.
 *
 * **Constraints:**
 *  1. The annotated method must not have any parameters!
 *  1. If multiple methods of a single fixture are annotated the invocation order is non-deterministic!
 *
 * @see DecisionTableFixture
 *
 * @since 2.0
 */
@Target(AnnotationTarget.FUNCTION)
annotation class BeforeRow
