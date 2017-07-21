package org.livingdoc.converters.collection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.api.conversion.Converter
import org.livingdoc.converters.DefaultTypeConverterContract

internal class SetConverterTest : CollectionConverterContract<Set<Any>>(), DefaultTypeConverterContract {

    override val cut = SetConverter()
    override val collectionClass = Set::class.java

    @Test
    fun `canConvertBoolean`() {
        val input = "true, false, false, true"
        val expected = setOf(true, false, false, true)

        assertThat(cut.convert(input, getParameterTypeConverter(setFake::class, "boolean"), null)).isEqualTo(expected)
    }

    @Test
    fun `canConvertInt`() {
        val input = "1, 2, 3, 4"
        val expected = setOf(1, 2, 3, 4)

        assertThat(cut.convert(input, getParameterTypeConverter(setFake::class, "integer"), null)).isEqualTo(expected)
    }

    @Test
    fun `converter can converted to Kotlin List`() {
        assertThat(cut.canConvertTo(Set::class.java)).isTrue()
    }

    internal class setFake {
        fun integer(@Converter(SetConverter::class) value: Set<Int>) {}

        fun boolean(@Converter(SetConverter::class) value: Set<Boolean>) {}
    }
}



