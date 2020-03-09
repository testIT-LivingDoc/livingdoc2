package org.livingdoc.converters.number

import org.livingdoc.api.conversion.Context
import java.math.BigDecimal
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * This converter converts a BigDecimal or a String to a Float number
 */
open class FloatConverter : AbstractNumberConverter<Float>() {

    companion object {
        const val NOT_A_NUMBER = "NaN"
        const val POSITIVE_INFINITY = "Infinity"
        const val NEGATIVE_INFINITY = "-Infinity"
        const val NEGATIVE_ZERO_STRING = "-0"
        const val NEGATIVE_ZERO_NUMBER = -0.0f
    }

    override val lowerBound: Float = -Float.MAX_VALUE
    override val upperBound: Float = Float.MAX_VALUE

    /**
     * This function returns the float representation of the content of value and provides the recognition of some edge
     * cases.
     *
     * @param value the string containing the value that should be converted
     */
    override fun convert(value: String, type: KType, context: Context): Float {
        return when (value) {
            NOT_A_NUMBER -> Float.NaN
            POSITIVE_INFINITY -> Float.POSITIVE_INFINITY
            NEGATIVE_INFINITY -> Float.NEGATIVE_INFINITY
            NEGATIVE_ZERO_STRING -> NEGATIVE_ZERO_NUMBER
            else -> super.convert(value, type, context)
        }
    }

    /**
     * This function returns the float representation of the given BigDecimal value.
     *
     * @param number the BigDecimal containing the value that should be converted
     */
    override fun convertToTarget(number: BigDecimal): Float = number.toFloat()

    override fun canConvertTo(targetType: KClass<*>): Boolean {
        return Float::class == targetType
    }
}
