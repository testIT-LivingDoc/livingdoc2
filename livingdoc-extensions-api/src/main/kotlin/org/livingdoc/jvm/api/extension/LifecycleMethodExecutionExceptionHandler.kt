package org.livingdoc.jvm.api.extension

import org.livingdoc.jvm.api.extension.context.ExtensionContext

/**
 * LifecycleMethodExecutionExceptionHandler defines the API for Extensions that wish to handle exceptions thrown during
 * the execution of @Before and @After lifecycle methods.
 *
 * # Constructor Requirements
 * Consult the documentation in [Extension] for details on constructor requirements.
 *
 * # Implementation Guidelines
 * An implementation of an exception handler method defined in this API must perform one of the following.
 *
 * 1. Rethrow the supplied Throwable as is, which is the default implementation.
 * 2. Swallow the supplied Throwable, thereby preventing propagation.
 * 3. Throw a new exception, potentially wrapping the supplied Throwable.
 *
 * If the supplied Throwable is swallowed by a handler method, subsequent handler methods for the same lifecycle will
 * not be invoked; otherwise, the corresponding handler method of the next registered
 * LifecycleMethodExecutionExceptionHandler (if there is one) will be invoked with any Throwable thrown by the previous
 * handler.
 */
interface LifecycleMethodExecutionExceptionHandler : Extension {
    fun handleBeforeMethodExecutionException(context: ExtensionContext, throwable: Throwable) {
        throw throwable
    }

    fun handleAfterMethodExecutionException(context: ExtensionContext, throwable: Throwable) {
        throw throwable
    }
}
