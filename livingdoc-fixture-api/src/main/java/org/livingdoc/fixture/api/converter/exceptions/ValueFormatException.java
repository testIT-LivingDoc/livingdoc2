package org.livingdoc.fixture.api.converter.exceptions;

public class ValueFormatException extends ConversionException {

    private static final String MESSAGE = "The value '%s' does not match the expected format: %s";

    public ValueFormatException(String value, String format, Throwable cause) {
        super(String.format(MESSAGE, value, format), cause);
    }

}
