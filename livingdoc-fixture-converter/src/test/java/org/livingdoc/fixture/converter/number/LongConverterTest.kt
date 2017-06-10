package org.livingdoc.fixture.converter.number

import org.livingdoc.fixture.api.converter.TypeConverter


internal class LongConverterTest : NumberConverterContract<Long>() {

    val cut = LongConverter()

    override fun getCut(): TypeConverter<Long> = cut

    override fun getMinValue() = Long.MIN_VALUE
    override fun getNegativeValue() = -42L
    override fun getZeroValue() = 0L
    override fun getPositiveValue() = 42L
    override fun getMaxValue() = Long.MAX_VALUE

    override fun getEnglishValue() = "42,000.12" to 42000L
    override fun getGermanValue() = "42.000,12" to 42000L

}
