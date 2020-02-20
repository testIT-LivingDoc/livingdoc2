package org.livingdoc.jvm.engine

class ThrowableCollector {
    private val list: MutableList<Throwable> = mutableListOf()

    @Suppress("TooGenericExceptionCaught")
    fun execute(executable: () -> Unit) {
        try {
            executable.invoke()
        } catch (t: Throwable) {
            add(t)
        }
    }

    private fun add(throwable: Throwable) {
        this.list.add(throwable)
    }

    fun isEmpty() = list.isEmpty()

    val throwable get() = list.getOrNull(0) ?: throw IllegalStateException("The ThrowableCollector is empty")
}
