package org.livingdoc.converters.exceptions

/**
 * This exception is used whenever the format is not formed correctly
 */
class MalformedFormatException(
    cause: Throwable
) : RuntimeException("Custom format pattern is malformed: ${cause.message}", cause)
