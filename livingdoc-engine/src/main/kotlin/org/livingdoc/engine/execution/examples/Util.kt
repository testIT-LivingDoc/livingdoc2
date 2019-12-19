package org.livingdoc.engine.execution.examples

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
