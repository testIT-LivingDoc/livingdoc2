package org.livingdoc.fixture.converter.number


internal class DoubleConverterTest : NumberConverterContract<Double>() {

    override val cut = DoubleConverter()

    override val minValue = Double.MIN_VALUE
    override val negativeValue = -42.24
    override val zeroValue = 0.0
    override val positiveValue = 42.24
    override val maxValue = Double.MAX_VALUE

    override val englishValue = "42,000.24" to 42000.24
    override val germanValue = "42.000,24" to 42000.24

}
