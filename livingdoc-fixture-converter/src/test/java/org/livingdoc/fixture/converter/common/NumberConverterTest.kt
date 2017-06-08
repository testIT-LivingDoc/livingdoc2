package org.livingdoc.fixture.converter.common

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import utils.EnglishDefaultLocale

/**
 * Since there are a lot of sub-classes of [NumberConverter], this class
 * contains only one simple smoke test to demonstrate, that any kind of number
 * can be handled by this converter.
 */
@EnglishDefaultLocale
internal class NumberConverterTest {

    val cut = NumberConverter()

    @ParameterizedTest
    @ValueSource(strings = arrayOf(
            "0", "0.00", "0000",
            "100,000", "100,000.42",
            "100000", "100000.42"
    ))
    fun `all kinds of numbers can be converted without problems`(value: String) {
        cut.convert(value)
    }

}
