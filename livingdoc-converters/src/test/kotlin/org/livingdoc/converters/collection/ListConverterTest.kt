package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.Converter
import org.livingdoc.converters.DefaultTypeConverterContract

internal class ListConverterTest : CollectionConverterContract<List<Any>>(), DefaultTypeConverterContract {

    override val cut = ListConverter()
    override val collectionClass = List::class.java
    override val fixtureClass = listFake::class
    override val intExpectation = listOf(1, 2, 3, 4)
    override val booleanExpectation = listOf(true, false, false, true)

    internal class listFake {
        fun integer(@Converter(ListConverter::class) value: List<Int>) {}

        fun boolean(@Converter(ListConverter::class) value: List<Boolean>) {}
    }
}


