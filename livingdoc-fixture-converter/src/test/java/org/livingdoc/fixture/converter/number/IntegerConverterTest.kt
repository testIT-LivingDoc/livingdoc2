package org.livingdoc.fixture.converter.number

internal class IntegerConverterTest : NumberConverterContract<Int>(), BoundedNumberContract<Int> {

    override val cut = IntegerConverter()

    override val minValue = Int.MIN_VALUE
    override val negativeValue = -42
    override val zeroValue = 0
    override val positiveValue = 42
    override val maxValue = Int.MAX_VALUE

    override val englishValue = "42,000.12" to 42000
    override val germanValue = "42.000,12" to 42000

}
