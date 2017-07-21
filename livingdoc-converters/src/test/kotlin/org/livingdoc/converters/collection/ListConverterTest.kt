package org.livingdoc.converters.collection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.api.conversion.Converter
import org.livingdoc.converters.DefaultTypeConverterContract

internal class ListConverterTest : CollectionConverterContract<List<Any>>(), DefaultTypeConverterContract {

    override val cut = ListConverter()
    override val collectionClass = List::class.java

    @Test
    fun `canConvertBoolean`() {
        val input = "true, false, false, true"
        val expected = listOf(true, false, false, true)

        assertThat(cut.convert(input, getParameterTypeConverter(listFake::class, "boolean"), null)).isEqualTo(expected)
    }

    @Test
    fun `canConvertInt`() {
        val input = "1, 2, 3, 4"
        val expected = listOf(1, 2, 3, 4)

        assertThat(cut.convert(input, getParameterTypeConverter(listFake::class, "integer"), null)).isEqualTo(expected)
    }

    @Test
    fun `converter can converted to Kotlin List`() {
        assertThat(cut.canConvertTo(List::class.java)).isTrue()
    }

    internal class listFake {
        fun integer(@Converter(ListConverter::class) value: List<Int>) {}

        fun boolean(@Converter(ListConverter::class) value: List<Boolean>) {}
    }
}


