package org.livingdoc.engine

import org.livingdoc.engine.execution.ExecutionException
import org.livingdoc.engine.execution.Result
import org.livingdoc.fixture.api.binding.ExecutableDocument


/**
 * Interface for the interaction with LivingDoc. Instances can be created via the [LivingDocFactory].
 *
 * @since 2.0
 */
interface LivingDoc {

    /**
     * Executes the given document instance and returns the [Result]. The given document instance must be of a class
     * annotated with [ExecutableDocument].
     *
     * @param document the document to execute
     * @return the [Result] of the execution
     * @throws ExecutionException in case the execution failed in a way that did not produce a viable result
     * @since 2.0
     */
    @Throws(ExecutionException::class)
    fun execute(document: Any): Result

}
