package org.livingdoc.converters.exceptions

import org.livingdoc.api.conversion.ConversionException

/**
 * This exception is used whenever the format is not formed correctly
 */
class ColorFormatException(value: String?) : ConversionException("The color value $value is not valid. Either the " +
        "value is not defined in the property file or the value has been typed wrong.")
