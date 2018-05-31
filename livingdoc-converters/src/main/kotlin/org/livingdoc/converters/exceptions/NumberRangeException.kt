package org.livingdoc.converters.exceptions

import org.livingdoc.api.conversion.ConversionException


class NumberRangeException(
    value: Number,
    min: Number?,
    max: Number?
) : ConversionException("The number '$value' is not between '${min ?: "-Infinity"}' and '${max ?: "Infinity"}'!")
