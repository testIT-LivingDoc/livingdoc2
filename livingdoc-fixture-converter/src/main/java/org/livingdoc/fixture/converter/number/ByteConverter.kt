package org.livingdoc.fixture.converter.number

import java.math.BigDecimal


open class ByteConverter : AbstractNumberConverter<Byte>() {

    override val lowerBound: Byte = Byte.MIN_VALUE
    override val upperBound: Byte = Byte.MAX_VALUE

    override fun convertToTarget(number: BigDecimal): Byte = number.toByte()

}
