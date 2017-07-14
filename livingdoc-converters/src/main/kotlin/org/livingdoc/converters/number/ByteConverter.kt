package org.livingdoc.converters.number

import java.math.BigDecimal


open class ByteConverter : AbstractNumberConverter<Byte>() {

    override val lowerBound: Byte = Byte.MIN_VALUE
    override val upperBound: Byte = Byte.MAX_VALUE

    override fun convertToTarget(number: BigDecimal): Byte = number.toByte()

    override fun canConvertTo(targetType: Class<*>): Boolean {
        val isJavaObjectType = Byte::class.javaObjectType == targetType
        val isKotlinType = Byte::class.java == targetType
        return isJavaObjectType || isKotlinType
    }

}
