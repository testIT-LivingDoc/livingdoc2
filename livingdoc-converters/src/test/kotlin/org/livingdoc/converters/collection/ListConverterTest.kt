package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.Converter

internal class ListConverterTest : CollectionConverterContract() {

    override val cut = ListConverter()
    override val collectionClass = List::class.java
    override val fixtureClass = ListFake::class
    override val booleanInput: String = "true, false, false, true"
    override val intInput: String = "1, 2, 3, 4"
    override val intExpectation = listOf(1, 2, 3, 4)
    override val booleanExpectation = listOf(true, false, false, true)

    @Suppress("unused", "UNUSED_PARAMETER")
    internal class ListFake {

        @Converter(ListConverter::class)
        lateinit var integer: List<Int>

        fun boolean(@Converter(ListConverter::class) value: List<Boolean>) {}

        fun noType(@Converter(ListConverter::class) value: List<ListFake>) {}
    }
}
