package org.livingdoc.engine

import org.livingdoc.engine.execution.DocumentResult
import org.livingdoc.engine.execution.ExecutionException
import org.livingdoc.api.documents.ExecutableDocument


/**
 * Interface for the interaction with LivingDoc. Instances can be created via the [LivingDocFactory].
 *
 * @since 2.0
 */
interface LivingDoc {

    /**
     * Executes the given document class and returns the [DocumentResult]. The document's class must be annotated
     * with [ExecutableDocument].
     *
     * @param document the binding class of the document to execute
     * @return the [DocumentResult] of the execution
     * @throws ExecutionException in case the execution failed in a way that did not produce a viable result
     * @since 2.0
     */
    @Throws(ExecutionException::class)
    fun execute(document: Class<*>): DocumentResult

}
