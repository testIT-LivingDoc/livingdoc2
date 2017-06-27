package org.livingdoc.fixture.converter.number

import java.math.BigDecimal


open class ShortConverter : AbstractNumberConverter<Short>() {

    override val lowerBound: Short = Short.MIN_VALUE
    override val upperBound: Short = Short.MAX_VALUE

    override fun convertToTarget(number: BigDecimal): Short = number.toShort()

}
