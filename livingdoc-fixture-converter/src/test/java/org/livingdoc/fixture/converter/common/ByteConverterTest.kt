package org.livingdoc.fixture.converter.common

import org.livingdoc.fixture.api.converter.TypeConverter

internal class ByteConverterTest : NumberConverterContract<Byte>() {

    val cut = ByteConverter()

    override fun getCut(): TypeConverter<Byte> = cut

    override fun getMinValue() = Byte.MIN_VALUE
    override fun getNegativeValue() = (-42).toByte()
    override fun getZeroValue() = 0.toByte()
    override fun getPositiveValue() = 42.toByte()
    override fun getMaxValue() = Byte.MAX_VALUE

    override fun getEnglishValue() = "100.12" to 100.toByte()
    override fun getGermanValue() = "100,12" to 100.toByte()

}
