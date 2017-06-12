package org.livingdoc.fixture.converter.number

internal class NumberConverterTest : NumberConverterContract<Number>() {

    override val cut = NumberConverter()

    override val minValue = Double.MIN_VALUE
    override val negativeValue = -42.000000000001
    override val zeroValue = 0L
    override val positiveValue = 42.000000000001
    override val maxValue = Double.MAX_VALUE

    override val englishValue = "42,000.24" to 42000.24
    override val germanValue = "42.000,24" to 42000.24

}
