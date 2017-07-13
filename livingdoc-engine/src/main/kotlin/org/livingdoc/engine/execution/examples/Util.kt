package org.livingdoc.engine.execution.examples


fun executeWithBeforeAndAfter(before: () -> Unit, body: () -> Unit, after: () -> Unit) {
    var exception: Throwable? = null
    try {
        before.invoke()
        body.invoke()
    } catch (e: Throwable) {
        exception = e
    } finally {
        try {
            after.invoke()
        } catch (e: Throwable) {
            if (exception != null) {
                exception.addSuppressed(e)
            } else {
                exception = e
            }
        } finally {
            if (exception != null) {
                throw exception
            }
        }
    }
}
