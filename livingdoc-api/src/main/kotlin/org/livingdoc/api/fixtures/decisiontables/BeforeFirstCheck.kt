package org.livingdoc.api.fixtures.decisiontables

/**
 * Methods annotated with this annotation are always invoked before the first [Check] of each row is executed.
 * These methods are generally used to execute test code based on the [Input] values and store the results to be
 * evaluated by the [Check] methods. This is a purely optional mechanic! Depending on the test case, directly
 * executing that logic inside a [Check] method might be the best approach.
 *
 * **Constraints:**
 *  1. The annotated method must not have any parameters!
 *  1. If multiple methods of a single fixture are annotated the invocation order is non-deterministic!
 *
 *
 * @see DecisionTableFixture
 *
 * @see Check
 *
 * @since 2.0
 */
@Target(AnnotationTarget.FUNCTION)
annotation class BeforeFirstCheck
