package org.livingdoc.fixture.converter.exceptions

import org.livingdoc.fixture.api.converter.ConversionException


class NumberRangeException(
        value: Number,
        min: Number?,
        max: Number?
) : ConversionException("The number '$value' is not between '${min ?: "-Infinity"}' and '${max ?: "Infinity"}'!")
