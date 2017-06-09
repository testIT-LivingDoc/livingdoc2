package org.livingdoc.fixture.converter.common

import org.livingdoc.fixture.api.converter.TypeConverter

internal class ShortConverterTest : NumberConverterContract<Short>() {

    val cut = ShortConverter()

    override fun getCut(): TypeConverter<Short> = cut

    override fun getMinValue() = Short.MIN_VALUE
    override fun getNegativeValue() = (-42).toShort()
    override fun getZeroValue() = 0.toShort()
    override fun getPositiveValue() = 42.toShort()
    override fun getMaxValue() = Short.MAX_VALUE

    override fun getEnglishValue() = "10,000.12" to 10000.toShort()
    override fun getGermanValue() = "10.000,12" to 10000.toShort()

}
