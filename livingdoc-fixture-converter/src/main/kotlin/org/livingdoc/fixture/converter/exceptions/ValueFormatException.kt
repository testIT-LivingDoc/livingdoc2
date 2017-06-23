package org.livingdoc.fixture.converter.exceptions

import org.livingdoc.fixture.api.converter.ConversionException


class ValueFormatException(
        value: String,
        format: String,
        cause: Throwable
) : ConversionException("The value '$value' does not match the expected format: $format", cause)
