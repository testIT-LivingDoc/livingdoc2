package org.livingdoc.engine.execution

/**
 * Exceptions of this type are thrown in case an execution failed in a way that did not allow for a [Result] to be
 * produced.
 */
open class ExecutionException : RuntimeException {

    protected constructor(message: String) : super(message)

    protected constructor(message: String, cause: Throwable) : super(message, cause)

}
