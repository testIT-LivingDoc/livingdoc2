package org.livingdoc.engine.execution.examples

@Suppress("TooGenericExceptionCaught")
fun executeWithBeforeAndAfter(before: () -> Unit, body: () -> Unit, after: () -> Unit) {
    var exception: Throwable? = null
    try {
        before.invoke()
        body.invoke()
    } catch (e: Throwable) {
        exception = e
    } finally {
        runAfter(after, exception)
    }
}
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
