package org.livingdoc.converters.number

import java.math.BigDecimal

/**
 * This converter converts a BigDecimal to a Byte
 */
open class ByteConverter : AbstractNumberConverter<Byte>() {

    override val lowerBound: Byte = Byte.MIN_VALUE
    override val upperBound: Byte = Byte.MAX_VALUE

    /**
     * This function returns the Byte representation of the given BigDecimal value.
     *
     * @param number the BigDecimal containing the value that should be converted
     */
    override fun convertToTarget(number: BigDecimal): Byte = number.toByte()

    override fun canConvertTo(targetType: Class<*>): Boolean {
        val isJavaObjectType = Byte::class.javaObjectType == targetType
        val isKotlinType = Byte::class.java == targetType
        return isJavaObjectType || isKotlinType
    }
}
