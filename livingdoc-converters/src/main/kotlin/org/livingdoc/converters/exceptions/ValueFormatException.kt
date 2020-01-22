package org.livingdoc.converters.exceptions

import org.livingdoc.api.conversion.ConversionException

/**
 * This Exception is used whenever the given value doesn't match the required format.
 * @param value the value that doesn't match
 * @param format the format that should have been matched
 */
class ValueFormatException(
    value: String,
    format: String,
    cause: Throwable
) : ConversionException("The value '$value' does not match the expected format: $format", cause)
