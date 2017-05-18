package org.livingdoc.fixture.converter.exceptions;

public class MalformedFormatException extends RuntimeException {

    private static final String MESSAGE = "Custom format pattern is malformed: %s";

    public MalformedFormatException(Throwable cause) {
        super(String.format(MESSAGE, cause.getMessage()), cause);
    }

}
