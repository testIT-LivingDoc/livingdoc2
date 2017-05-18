package org.livingdoc.fixture.converter.exceptions;

import org.livingdoc.fixture.api.converter.ConversionException;


public class ValueFormatException extends ConversionException {

    private static final String MESSAGE = "The value '%s' does not match the expected format: %s";

    public ValueFormatException(String value, String format, Throwable cause) {
        super(String.format(MESSAGE, value, format), cause);
    }

}
