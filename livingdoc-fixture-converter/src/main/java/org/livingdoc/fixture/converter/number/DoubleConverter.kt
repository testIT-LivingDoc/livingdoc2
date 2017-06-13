package org.livingdoc.fixture.converter.number

import java.lang.reflect.AnnotatedElement


open class DoubleConverter : AbstractNumberConverter<Double>() {

    companion object {
        const val NOT_A_NUMBER = "NaN"
        const val POSITIVE_INFINITY = "Infinity"
        const val NEGATIVE_INFINITY = "-Infinity"
    }

    override fun convert(value: String, element: AnnotatedElement?): Double {
        return when (value) {
            NOT_A_NUMBER -> Double.NaN
            POSITIVE_INFINITY -> Double.POSITIVE_INFINITY
            NEGATIVE_INFINITY -> Double.NEGATIVE_INFINITY
            else -> super.convert(value, element)
        }
    }

    override fun convertToTarget(number: Number): Double = number.toDouble()

}
