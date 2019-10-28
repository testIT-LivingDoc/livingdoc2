package org.livingdoc.example

class TextFunctions {

    fun concStrings(a: String, b: String): String {
        return a + b
    }

    fun nullifyString(): String {
        return "0.0F"
    }

    fun multiline(a: String, b: String): String {
        return "line 1: " + a + ", line 2: " + b
    }
}
