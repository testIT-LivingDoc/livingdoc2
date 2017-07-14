package org.livingdoc.converters.number

import java.math.BigDecimal


open class BigDecimalConverter : AbstractNumberConverter<BigDecimal>() {

    override val lowerBound: BigDecimal? = null
    override val upperBound: BigDecimal? = null

    override fun convertToTarget(number: BigDecimal): BigDecimal = number

    override fun canConvertTo(targetType: Class<*>) = BigDecimal::class.java == targetType

}
