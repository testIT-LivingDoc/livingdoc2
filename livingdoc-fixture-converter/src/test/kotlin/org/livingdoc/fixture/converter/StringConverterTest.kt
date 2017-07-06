package org.livingdoc.fixture.converter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


internal class StringConverterTest {

    val cut = StringConverter()

    @ParameterizedTest(name = "\"{0}\"")
    @ValueSource(strings = arrayOf("", " ", "foo", "foo bar"))
    fun `any string value is returned as is`(value: String) {
        assertThat(cut.convert(value)).isEqualTo(value)
    }

    @Test
    fun `converter can converted to Kotlin String`() {
        assertThat(cut.canConvertTo(String::class.java)).isTrue()
    }

}
