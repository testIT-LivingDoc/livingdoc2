package org.livingdoc.fixture.converter.number

internal class ByteConverterTest : NumberConverterContract<Byte>() {

    override val cut = ByteConverter()

    override val minValue = Byte.MIN_VALUE
    override val negativeValue = (-42).toByte()
    override val zeroValue = 0.toByte()
    override val positiveValue = 42.toByte()
    override val maxValue = Byte.MAX_VALUE

    override val englishValue = "100.12" to 100.toByte()
    override val germanValue = "100,12" to 100.toByte()

}
