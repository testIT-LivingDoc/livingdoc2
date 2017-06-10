package org.livingdoc.fixture.converter.number

import org.livingdoc.fixture.api.converter.TypeConverter


internal class DoubleConverterTest : NumberConverterContract<Double>() {

    val cut = DoubleConverter()

    override fun getCut(): TypeConverter<Double> = cut

    override fun getMinValue() = Double.MIN_VALUE
    override fun getNegativeValue() = -42.24
    override fun getZeroValue() = 0.0
    override fun getPositiveValue() = 42.24
    override fun getMaxValue() = Double.MAX_VALUE

    override fun getEnglishValue() = "42,000.24" to 42000.24
    override fun getGermanValue() = "42.000,24" to 42000.24

}
