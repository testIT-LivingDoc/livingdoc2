package org.livingdoc.converters.exceptions

import org.livingdoc.api.conversion.ConversionException


class ValueFormatException(
        value: String,
        format: String,
        cause: Throwable
) : ConversionException("The value '$value' does not match the expected format: $format", cause)
