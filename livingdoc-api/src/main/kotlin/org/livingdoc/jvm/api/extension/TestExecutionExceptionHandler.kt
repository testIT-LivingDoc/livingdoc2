package org.livingdoc.jvm.api.extension

import org.livingdoc.jvm.api.extension.context.ExtensionContext

/**
 * TestExecutionExceptionHandler defines the API for Extensions that wish to handle exceptions thrown during test
 * execution. In this context, test execution refers to the physical invocation of a livingdoc test method and not to
 * any test-level extensions or callbacks.
 *
 * # Constructor Requirements
 * Consult the documentation in [Extension] for details on constructor requirements.
 */
interface TestExecutionExceptionHandler : Extension {
    /**
     * Handle the supplied throwable.
     *
     * Implementors must perform one of the following.
     *
     * 1. Swallow the supplied throwable, thereby preventing propagation.
     * 2. Rethrow the supplied throwable as is.
     * 3. Throw a new exception, potentially wrapping the supplied throwable.
     *
     * If the supplied throwable is swallowed, subsequent TestExecutionExceptionHandlers will not be invoked; otherwise,
     * the next registered TestExecutionExceptionHandler (if there is one) will be invoked with any Throwable thrown by
     * this handler.
     */
    @Throws(Throwable::class)
    fun handleTestExecutionException(context: ExtensionContext, throwable: Throwable)
}
