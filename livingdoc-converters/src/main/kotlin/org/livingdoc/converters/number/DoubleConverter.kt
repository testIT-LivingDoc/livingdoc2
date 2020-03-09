package org.livingdoc.converters.number

import org.livingdoc.api.conversion.Context
import java.math.BigDecimal
import kotlin.reflect.KClass
import kotlin.reflect.KType

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
    override fun convert(value: String, type: KType, context: Context): Double {
        return when (value) {
            NOT_A_NUMBER -> Double.NaN
            POSITIVE_INFINITY -> Double.POSITIVE_INFINITY
            NEGATIVE_INFINITY -> Double.NEGATIVE_INFINITY
            NEGATIVE_ZERO_STRING -> NEGATIVE_ZERO_NUMBER
            else -> super.convert(value, type, context)
        }
    }

    /**
     * This function returns the Double representation of the given BigDecimal value.
     *
     * @param number the BigDecimal containing the value that should be converted
     */
    override fun convertToTarget(number: BigDecimal): Double = number.toDouble()

    override fun canConvertTo(targetType: KClass<*>): Boolean {
        return Double::class == targetType
    }
}
