package org.livingdoc.fixture.converter.number

import java.math.BigDecimal

internal class BigDecimalConverterTest : NumberConverterContract<BigDecimal>() {

    override val cut = BigDecimalConverter()

    override val minValue = BigDecimal.valueOf(Double.MIN_VALUE)
    override val negativeValue = BigDecimal.valueOf(-42.24)
    override val zeroValue = BigDecimal.ZERO
    override val positiveValue = BigDecimal.valueOf(42.24)
    override val maxValue = BigDecimal.valueOf(Double.MAX_VALUE)

    override val englishValue = "42,000,000,000,000.12" to BigDecimal("42000000000000.12")
    override val germanValue = "42.000.000.000.000,12" to BigDecimal("42000000000000.12")

}