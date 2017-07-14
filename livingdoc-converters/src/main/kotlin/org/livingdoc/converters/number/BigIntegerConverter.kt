package org.livingdoc.converters.number

import java.math.BigDecimal
import java.math.BigInteger


open class BigIntegerConverter : AbstractNumberConverter<BigInteger>() {

    override val lowerBound: BigInteger? = null
    override val upperBound: BigInteger? = null

    override fun convertToTarget(number: BigDecimal): BigInteger = number.toBigInteger()

    override fun canConvertTo(targetType: Class<*>) = BigInteger::class.java == targetType

}
