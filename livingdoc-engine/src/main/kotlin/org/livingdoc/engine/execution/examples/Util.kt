package org.livingdoc.engine.execution.examples

/**
 * This function executes a before hook, the main function and an after hook in this order.
 * It ensures, that the after hook is executed even if there was an exception in before or main function
 * @param before the function that is called before the main part
 * @param body the main function
 * @param after the function that is called after the main part
 */
@Suppress("TooGenericExceptionCaught")
fun <T> executeWithBeforeAndAfter(before: () -> Unit, body: () -> T, after: () -> Unit): T {
    var exception: Throwable? = null
    val value =
        try {
            before.invoke()
            body.invoke()
        } catch (e: Throwable) {
            exception = e
            null
        } finally {
            runAfter(after, exception)
        }
    return value ?: throw IllegalStateException()
}

/**
 *  This function handles the execution of the after hook. It manages occurring exceptions too.
 *  @param after the after hook that should be executed
 *  @param exception a possibly occurred exception in the before or main function
 */
@Suppress("TooGenericExceptionCaught")
private fun runAfter(after: () -> Unit, exception: Throwable?) {
    var originalException = exception
    try {
        after.invoke()
    } catch (afterException: Throwable) {
        if (originalException != null) {
            originalException.addSuppressed(afterException)
        } else {
            originalException = afterException
        }
    } finally {
        if (originalException != null) {
            throw originalException
        }
    }
}
