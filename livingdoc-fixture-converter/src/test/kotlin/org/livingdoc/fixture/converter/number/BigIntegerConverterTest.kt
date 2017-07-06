package org.livingdoc.fixture.converter.number

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger

internal class BigIntegerConverterTest : NumberConverterContract<BigInteger>() {

    override val cut = BigIntegerConverter()

    override val minValue = BigInteger.valueOf(Long.MIN_VALUE)
    override val negativeValue = BigInteger.valueOf(-42L)
    override val zeroValue = BigInteger.ZERO
    override val positiveValue = BigInteger.valueOf(42L)
    override val maxValue = BigInteger.valueOf(Long.MAX_VALUE)

    override val englishValue = "42,000,000,000,000.12" to BigInteger("42000000000000")
    override val germanValue = "42.000.000.000.000,12" to BigInteger("42000000000000")

    @Test
    fun `converter can converted to BigInteger`() {
        assertThat(cut.canConvertTo(BigInteger::class.java)).isTrue()
    }

}