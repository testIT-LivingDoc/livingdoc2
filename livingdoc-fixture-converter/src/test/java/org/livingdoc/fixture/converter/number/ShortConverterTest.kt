package org.livingdoc.fixture.converter.number

internal class ShortConverterTest : NumberConverterContract<Short>() {

    override val cut = ShortConverter()

    override val minValue = Short.MIN_VALUE
    override val negativeValue = (-42).toShort()
    override val zeroValue = 0.toShort()
    override val positiveValue = 42.toShort()
    override val maxValue = Short.MAX_VALUE

    override val englishValue = "10,000.12" to 10000.toShort()
    override val germanValue = "10.000,12" to 10000.toShort()

}
