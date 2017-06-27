package org.livingdoc.fixture.converter.number

import java.math.BigDecimal


open class BigDecimalConverter : AbstractNumberConverter<BigDecimal>() {

    override val lowerBound: BigDecimal? = null
    override val upperBound: BigDecimal? = null

    override fun convertToTarget(number: BigDecimal): BigDecimal = number

}
