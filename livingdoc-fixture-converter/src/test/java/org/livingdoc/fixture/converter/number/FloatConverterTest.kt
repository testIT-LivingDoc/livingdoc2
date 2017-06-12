package org.livingdoc.fixture.converter.number

internal class FloatConverterTest : NumberConverterContract<Float>() {

    override val cut = FloatConverter()

    override val minValue = Float.MIN_VALUE
    override val negativeValue = -42.24f
    override val zeroValue = 0.0f
    override val positiveValue = 42.24f
    override val maxValue = Float.MAX_VALUE

    override val englishValue = "42,000.24" to 42000.24f
    override val germanValue = "42.000,24" to 42000.24f

}
