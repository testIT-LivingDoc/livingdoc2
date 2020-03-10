package org.livingdoc.api.conversion

/**
 * This exception type (or one of its sub-classes) is thrown in cases where a [TypeConverter] could not convert a value
 * given to it. Since this generally means the value was 'wrong', this exception should be treated as a terminal
 * condition for the current test case.
 *
 * @since 2.0
 */
open class ConversionException : RuntimeException {
    /**
     * This Constructor takes the provided message and handles it like the RuntimeException
     * @param message the exception message
     */
    constructor(message: String) : super(message)

    /**
     * This Constructor takes the provided message cause and handles it like the RuntimeException
     * @param message the exception message
     * @param cause the cause
     */
    constructor(message: String, cause: Throwable) : super(message, cause)

    /**
     * This Constructor takes the provided cause and handles it like the RuntimeException
     * @param cause the cause
     */
    constructor(cause: Throwable) : super(cause)
}
