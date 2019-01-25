package org.livingdoc.converters.number

import java.lang.reflect.AnnotatedElement
import java.math.BigDecimal

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

    override fun convert(value: String, element: AnnotatedElement?, documentClass: Class<*>?): Float {
        return when (value) {
            NOT_A_NUMBER -> Float.NaN
            POSITIVE_INFINITY -> Float.POSITIVE_INFINITY
            NEGATIVE_INFINITY -> Float.NEGATIVE_INFINITY
            NEGATIVE_ZERO_STRING -> NEGATIVE_ZERO_NUMBER
            else -> super.convert(value, element, documentClass)
        }
    }

    override fun convertToTarget(number: BigDecimal): Float = number.toFloat()

    override fun canConvertTo(targetType: Class<*>): Boolean {
        val isJavaObjectType = Float::class.javaObjectType == targetType
        val isKotlinType = Float::class.java == targetType
        return isJavaObjectType || isKotlinType
    }
}
