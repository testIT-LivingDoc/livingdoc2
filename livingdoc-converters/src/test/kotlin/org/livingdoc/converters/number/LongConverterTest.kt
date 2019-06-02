package org.livingdoc.converters.number

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.converters.DefaultTypeConverterContract

internal class LongConverterTest : BoundedNumberConverterContract<Long>(), DefaultTypeConverterContract {

    override val cut = LongConverter()

    override val minValue = Long.MIN_VALUE
    override val negativeValue = -42L
    override val zeroValue = 0L
    override val positiveValue = 42L
    override val maxValue = Long.MAX_VALUE

    override val englishValue = "42,000.12" to 42000L
    override val germanValue = "42.000,12" to 42000L

    @Test fun `converter can converted to Kotlin Long`() {
        assertThat(cut.canConvertTo(Long::class.java)).isTrue()
    }
}
