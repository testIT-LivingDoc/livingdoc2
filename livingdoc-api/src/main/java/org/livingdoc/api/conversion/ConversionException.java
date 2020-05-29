package org.livingdoc.api.conversion;

/**
 * This exception type (or one of its sub-classes) is thrown in cases where a {@link TypeConverter} could not convert a
 * value given to it. Since this generally means the value was 'wrong', this exception should be treated as a terminal
 * condition for the current test case.
 *
 * @since 2.0
 */
public class ConversionException extends RuntimeException {
    /**
     * This Constructor takes the provided message and handles it like the RuntimeException
     * @param message the exception message
     */
    public ConversionException(String message) {
        super(message);
    }

    /**
     * This Constructor takes the provided message cause and handles it like the RuntimeException
     * @param message the exception message
     * @param cause the cause
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * This Constructor takes the provided cause and handles it like the RuntimeException
     * @param cause the cause
     */
    public ConversionException(Throwable cause) {
        super(cause);
    }

}
