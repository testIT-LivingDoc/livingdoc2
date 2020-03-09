package org.livingdoc.converters.number

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.converters.DefaultTypeConverterContract
import org.livingdoc.converters.convertValueOnly

internal class FloatConverterTest : BoundedNumberConverterContract<Float>(), DefaultTypeConverterContract {

    override val cut = FloatConverter()

    override val minValue = Float.MIN_VALUE
    override val negativeValue = -42.24f
    override val zeroValue = 0.0f
    override val positiveValue = 42.24f
    override val maxValue = Float.MAX_VALUE

    override val englishValue = "42,000.24" to 42000.24f
    override val germanValue = "42.000,24" to 42000.24f

    @Test
    fun `not a number is handled correctly`() {
        cut.convertValueOnly(Float.NaN.toString())
    }

    @Test
    fun `positive infinity is handled correctly`() {
        cut.convertValueOnly(Float.POSITIVE_INFINITY.toString())
    }

    @Test
    fun `negative infinity is handled correctly`() {
        cut.convertValueOnly(Float.NEGATIVE_INFINITY.toString())
    }

    @Test
    fun `converter can converted to Kotlin Float`() {
        assertThat(cut.canConvertTo(Float::class)).isTrue()
    }

    @Test
    fun `converter can handle float specific -0 case`() {
        assertThat(cut.convertValueOnly("-0")).isEqualTo(-0.0f)
    }
}
