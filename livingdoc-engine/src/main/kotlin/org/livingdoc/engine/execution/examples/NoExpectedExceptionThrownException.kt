package org.livingdoc.engine.execution.examples

/**
 *
 */
internal class NoExpectedExceptionThrownException : AssertionError(
    "No exception thrown but exception was expected to be thrown by fixture"
)
