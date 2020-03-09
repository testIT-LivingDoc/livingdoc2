package org.livingdoc.converters.number

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.converters.DefaultTypeConverterContract

internal class IntegerConverterTest : BoundedNumberConverterContract<Int>(), DefaultTypeConverterContract {

    override val cut = IntegerConverter()

    override val minValue = Int.MIN_VALUE
    override val negativeValue = -42
    override val zeroValue = 0
    override val positiveValue = 42
    override val maxValue = Int.MAX_VALUE

    override val englishValue = "42,000.12" to 42000
    override val germanValue = "42.000,12" to 42000

    @Test fun `converter can converted to Kotlin Int`() {
        assertThat(cut.canConvertTo(Int::class)).isTrue()
    }
}
