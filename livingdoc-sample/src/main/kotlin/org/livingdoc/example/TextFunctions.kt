package org.livingdoc.example

/**
 * Some string handling functions
 *
 * This will be our second system under test (SUT) in this project
 */
class TextFunctions {

    /**
     * @param a the first string
     * @param b the second string
     * @return the concatenation of both strings
     */
    fun concStrings(a: String, b: String): String {
        return a + b
    }

    /**
     * @return returns a string representing floating-point zero
     */
    fun nullifyString(): String {
        return "0.0F"
    }

    /**
     * @param a the first line
     * @param b the second line
     * @return a multiline representation of both strings
     */
    fun multiline(a: String, b: String): String {
        return "line 1: " + a + ", line 2: " + b
    }
}
