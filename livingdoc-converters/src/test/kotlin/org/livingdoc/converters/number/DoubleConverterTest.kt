package org.livingdoc.converters.number

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.converters.DefaultTypeConverterContract

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
        cut.convert(Double.NaN.toString(), null, null)
    }

    @Test fun `positive infinity is handled correctly`() {
        cut.convert(Double.POSITIVE_INFINITY.toString(), null, null)
    }

    @Test fun `negative infinity is handled correctly`() {
        cut.convert(Double.NEGATIVE_INFINITY.toString(), null, null)
    }

    @Test fun `converter can converted to Kotlin Double`() {
        assertThat(cut.canConvertTo(Double::class.java)).isTrue()
    }

    @Test fun `converter can handle double specific -0 case`() {
        assertThat(cut.convert("-0", null, null)).isEqualTo(-0.0)
    }

}
