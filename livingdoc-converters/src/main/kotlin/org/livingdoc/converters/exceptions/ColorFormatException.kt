package org.livingdoc.converters.exceptions

import org.livingdoc.api.conversion.ConversionException

class ColorFormatException(value: String?) : ConversionException("The color value $value is not valid. Either the " +
        "value is not defined in the property file or the value has been typed wrong.")
