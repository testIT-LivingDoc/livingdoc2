package org.livingdoc.fixture.converter.number

import java.lang.reflect.AnnotatedElement
import java.math.BigDecimal

open class FloatConverter : AbstractNumberConverter<Float>() {

    companion object {
        const val NOT_A_NUMBER = "NaN"
        const val POSITIVE_INFINITY = "Infinity"
        const val NEGATIVE_INFINITY = "-Infinity"
    }

    override val lowerBound: Float = -Float.MAX_VALUE
    override val upperBound: Float = Float.MAX_VALUE

    override fun convert(value: String, element: AnnotatedElement?): Float {
        return when (value) {
            NOT_A_NUMBER -> Float.NaN
            POSITIVE_INFINITY -> Float.POSITIVE_INFINITY
            NEGATIVE_INFINITY -> Float.NEGATIVE_INFINITY
            else -> super.convert(value, element)
        }
    }

    override fun convertToTarget(number: BigDecimal): Float = number.toFloat()

}
