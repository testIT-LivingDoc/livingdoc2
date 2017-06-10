package org.livingdoc.fixture.converter.number

import org.livingdoc.fixture.api.converter.TypeConverter

internal class IntegerConverterTest : NumberConverterContract<Int>() {

    val cut = IntegerConverter()

    override fun getCut(): TypeConverter<Int> = cut

    override fun getMinValue() = Int.MIN_VALUE
    override fun getNegativeValue() = -42
    override fun getZeroValue() = 0
    override fun getPositiveValue() = 42
    override fun getMaxValue() = Int.MAX_VALUE

    override fun getEnglishValue() = "42,000.12" to 42000
    override fun getGermanValue() = "42.000,12" to 42000

}
