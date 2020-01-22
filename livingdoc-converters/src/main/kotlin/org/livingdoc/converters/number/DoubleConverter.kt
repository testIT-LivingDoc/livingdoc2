package org.livingdoc.converters.number

import java.lang.reflect.AnnotatedElement
import java.math.BigDecimal

/**
 * This converter converts a BigDecimal or a String to a Double number
 */
open class DoubleConverter : AbstractNumberConverter<Double>() {

    companion object {
        const val NOT_A_NUMBER = "NaN"
        const val POSITIVE_INFINITY = "Infinity"
        const val NEGATIVE_INFINITY = "-Infinity"
        const val NEGATIVE_ZERO_STRING = "-0"
        const val NEGATIVE_ZERO_NUMBER = -0.0
    }

    override val lowerBound: Double = -Double.MAX_VALUE
    override val upperBound: Double = Double.MAX_VALUE

    /**
     * This function returns the Double representation of the content of value and provides the recognition of some edge
     * cases.
     *
     * @param value the string containing the value that should be converted
     */
    override fun convert(value: String, element: AnnotatedElement?, documentClass: Class<*>?): Double {
        return when (value) {
            NOT_A_NUMBER -> Double.NaN
            POSITIVE_INFINITY -> Double.POSITIVE_INFINITY
            NEGATIVE_INFINITY -> Double.NEGATIVE_INFINITY
            NEGATIVE_ZERO_STRING -> NEGATIVE_ZERO_NUMBER
            else -> super.convert(value, element, documentClass)
        }
    }

    /**
     * This function returns the Double representation of the given BigDecimal value.
     *
     * @param number the BigDecimal containing the value that should be converted
     */
    override fun convertToTarget(number: BigDecimal): Double = number.toDouble()

    override fun canConvertTo(targetType: Class<*>): Boolean {
        val isJavaObjectType = Double::class.javaObjectType == targetType
        val isKotlinType = Double::class.java == targetType
        return isJavaObjectType || isKotlinType
    }
}
