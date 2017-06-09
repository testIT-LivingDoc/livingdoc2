package org.livingdoc.fixture.converter.common

import org.livingdoc.fixture.api.converter.TypeConverter

internal class NumberConverterTest : NumberConverterContract<Number>() {

    val cut = NumberConverter()

    override fun getCut(): TypeConverter<Number> = cut

    override fun getMinValue() = Double.MIN_VALUE
    override fun getNegativeValue() = -42.000000000001
    override fun getZeroValue() = 0L
    override fun getPositiveValue() = 42.000000000001
    override fun getMaxValue() = Double.MAX_VALUE

    override fun getEnglishValue() = "42,000.24" to 42000.24
    override fun getGermanValue() = "42.000,24" to 42000.24

}
