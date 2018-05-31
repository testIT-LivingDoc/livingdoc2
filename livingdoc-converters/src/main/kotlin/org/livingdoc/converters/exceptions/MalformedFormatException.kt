package org.livingdoc.converters.exceptions

class MalformedFormatException(
    cause: Throwable
) : RuntimeException("Custom format pattern is malformed: ${cause.message}", cause)
