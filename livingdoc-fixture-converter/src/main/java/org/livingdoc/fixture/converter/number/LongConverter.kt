package org.livingdoc.fixture.converter.number

import java.math.BigDecimal


open class LongConverter : AbstractNumberConverter<Long>() {

    override val lowerBound: Long = Long.MIN_VALUE
    override val upperBound: Long = Long.MAX_VALUE

    override fun convertToTarget(number: BigDecimal): Long = number.toLong()

}
