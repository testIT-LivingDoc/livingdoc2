package org.livingdoc.converters.number

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

/**
 * This converter converts a BigDecimal to a BigInteger
 */
open class BigIntegerConverter : AbstractNumberConverter<BigInteger>() {

    override val lowerBound: BigInteger? = null
    override val upperBound: BigInteger? = null

    /**
     * This function returns the BigInteger representation of the given BigDecimal value.
     *
     * @param number the BigDecimal containing the value that should be converted
     */
    override fun convertToTarget(number: BigDecimal): BigInteger = number.toBigInteger()

    override fun canConvertTo(targetType: KClass<*>) = BigInteger::class == targetType
}
