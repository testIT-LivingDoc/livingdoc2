package org.livingdoc.fixture.converter.exceptions

class MalformedFormatException(
        cause: Throwable
) : RuntimeException("Custom format pattern is malformed: ${cause.message}", cause)
