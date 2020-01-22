package org.livingdoc.converters.exceptions

import org.livingdoc.api.conversion.ConversionException

/**
 * This Exception is used whenever the given value is not in the required range.
 * @param value the value that doesn't match
 * @param min the lower bound of the range
 * @param max the upper bound of the range
 */
class NumberRangeException(
    value: Number,
    min: Number?,
    max: Number?
) : ConversionException("The number '$value' is not between '${min ?: "-Infinity"}' and '${max ?: "Infinity"}'!")
