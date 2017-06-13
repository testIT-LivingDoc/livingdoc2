package org.livingdoc.fixture.converter.number

import java.lang.reflect.AnnotatedElement


open class DoubleConverter : AbstractNumberConverter<Double>() {

    companion object {
        const val NOT_A_NUMBER = "NaN"
        const val POSITIVE_INFINITY = "Infinity"
        const val NEGATIVE_INFINITY = "-Infinity"
    }

    override fun convert(value: String, element: AnnotatedElement?): Double {
        when (value) {
            NOT_A_NUMBER -> return Double.NaN
            POSITIVE_INFINITY -> return Double.POSITIVE_INFINITY
            NEGATIVE_INFINITY -> return Double.NEGATIVE_INFINITY
            else -> return super.convert(value, element)
        }
    }

    override fun convertToTarget(number: Number): Double = number.toDouble()

}
