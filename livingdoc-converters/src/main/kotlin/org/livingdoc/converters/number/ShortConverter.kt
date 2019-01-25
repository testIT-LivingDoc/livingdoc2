package org.livingdoc.converters.number

import java.math.BigDecimal

open class ShortConverter : AbstractNumberConverter<Short>() {

    override val lowerBound: Short = Short.MIN_VALUE
    override val upperBound: Short = Short.MAX_VALUE

    override fun convertToTarget(number: BigDecimal): Short = number.toShort()

    override fun canConvertTo(targetType: Class<*>): Boolean {
        val isJavaObjectType = Short::class.javaObjectType == targetType
        val isKotlinType = Short::class.java == targetType
        return isJavaObjectType || isKotlinType
    }
}
