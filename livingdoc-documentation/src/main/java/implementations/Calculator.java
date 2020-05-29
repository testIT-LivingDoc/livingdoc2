package implementations;

/**
 * A simple calculator, just as in the sample but here written in JAVA
 *
 * This will be our first system under test (SUT) in this project
 */

public class Calculator {

    /**
     * @param a the dividend
     * @param b the divisor
     * @return the quotient of two numbers
     */

    public double sum(double a, double b) {
        return a + b;
    }

    /**
     * @param a the minuend
     * @param b the subtrahend
     * @return the difference of two numbers
     */

    public double diff(double a, double b) {
        return a - b;
    }

}
