package org.livingdoc.example

/**
 * A simple calculator implementation
 *
 * This will be our first system under test (SUT) in this project
 */
class Calculator {

    /**
     * @return the sum of two numbers
     */
    fun sum(a: Float, b: Float): Float {
        return a + b
    }

    /**
     * @param a the minuend
     * @param b the subtrahend
     * @return the difference of two numbers
     */
    fun diff(a: Float, b: Float): Float {
        return a - b
    }

    /**
     * @return the product of two numbers
     */
    fun multiply(a: Float, b: Float): Float {
        return a * b
    }

    /**
     * @param a the dividend
     * @param b the divisor
     * @return the quotient of two numbers
     */
    fun divide(a: Float, b: Float): Float {
        return a / b
    }
}
