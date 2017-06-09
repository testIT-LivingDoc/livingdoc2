package org.livingdoc.fixture.converter.common

import org.livingdoc.fixture.api.converter.TypeConverter

internal class FloatConverterTest : NumberConverterContract<Float>() {

    val cut = FloatConverter()

    override fun getCut(): TypeConverter<Float> = cut

    override fun getMinValue() = Float.MIN_VALUE
    override fun getNegativeValue() = -42.24f
    override fun getZeroValue() = 0.0f
    override fun getPositiveValue() = 42.24f
    override fun getMaxValue() = Float.MAX_VALUE

    override fun getEnglishValue() = "42,000.24" to 42000.24f
    override fun getGermanValue() = "42.000,24" to 42000.24f

}
