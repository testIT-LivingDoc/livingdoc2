package org.livingdoc.engine

import org.livingdoc.engine.execution.DocumentSelector
import org.livingdoc.engine.execution.ExecutionException
import org.livingdoc.engine.execution.Result


/**
 * Interface for the interaction with LivingDoc. Instances can be created via the [LivingDocFactory].
 *
 * @since 2.0
 */
interface LivingDoc {

    /**
     * Tries to find a matching executable document for the given [DocumentSelector] and executes it.
     *
     * @param selector a [DocumentSelector] of the document to execute
     * @return the [Result] of the execution
     * @throws ExecutionException in case the execution failed in a way that did not produce a viable result
     * @since 2.0
     */
    @Throws(ExecutionException::class)
    fun execute(selector: DocumentSelector): Result

}
