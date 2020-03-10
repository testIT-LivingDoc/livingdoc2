package org.livingdoc.api

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.documents.Group

/**
 * Methods annotated with this annotation are invoked before all other methods or nested classes are invoked. This
 * method can be used in [ExecutableDocuments][ExecutableDocument], [Groups][Group] and Fixtures.
 *
 * **Constraints:**
 *  1. The annotated method must not have any parameters!
 *  1. If multiple methods of a single fixture are annotated the invocation order is non-deterministic!
 *
 * @see ExecutableDocument
 * @See Group
 *
 * @since 2.0
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Before
