package org.livingdoc.converters.number

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.converters.DefaultTypeConverterContract
import org.livingdoc.converters.convertValueOnly

internal class DoubleConverterTest : BoundedNumberConverterContract<Double>(), DefaultTypeConverterContract {

    override val cut = DoubleConverter()

    override val minValue = -Double.MIN_VALUE
    override val negativeValue = -42.24
    override val zeroValue = 0.0
    override val positiveValue = 42.24
    override val maxValue = Double.MAX_VALUE

    override val englishValue = "42,000.24" to 42000.24
    override val germanValue = "42.000,24" to 42000.24

    @Test fun `not a number is handled correctly`() {
        cut.convertValueOnly(Double.NaN.toString())
    }

    @Test fun `positive infinity is handled correctly`() {
        cut.convertValueOnly(Double.POSITIVE_INFINITY.toString())
    }

    @Test fun `negative infinity is handled correctly`() {
        cut.convertValueOnly(Double.NEGATIVE_INFINITY.toString())
    }

    @Test fun `converter can converted to Kotlin Double`() {
        assertThat(cut.canConvertTo(Double::class)).isTrue()
    }

    @Test fun `converter can handle double specific -0 case`() {
        assertThat(cut.convertValueOnly("-0")).isEqualTo(-0.0)
    }
}
