package org.livingdoc.example

/**
 * A simple calculator implementation
 *
 * This will be our first system under test (SUT) in this project
 */
class Divider {

    /**
     * @param a the dividend
     * @param b the divisor
     * @return the quotient of two numbers
     */
    @Throws(IllegalArgumentException::class)
    fun divide(a: Float, b: Float): Float {
        if (b == 0.0f) {
            throw IllegalArgumentException("Thrown by Divider")
        }
        return a / b
    }
}
