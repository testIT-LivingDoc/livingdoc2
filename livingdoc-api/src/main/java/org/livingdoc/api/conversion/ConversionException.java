package org.livingdoc.api.conversion;

/**
 * This exception type (or one of its sub-classes) is thrown in cases where a {@link TypeConverter} could not convert a
 * value given to it. Since this generally means the value was 'wrong', this exception should be treated as a terminal
 * condition for the current test case.
 *
 * @since 2.0
 */
public class ConversionException extends RuntimeException {

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversionException(Throwable cause) {
        super(cause);
    }

}
