package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.Converter

internal class SetConverterTest : CollectionConverterContract() {

    override val cut = SetConverter()
    override val collectionClass = Set::class
    override val fixtureClass = SetFake::class
    override val booleanInput: String = "true, false, false, true"
    override val intInput: String = "1, 2, 3, 4"
    override val intExpectation = setOf(1, 2, 3, 4)
    override val booleanExpectation = setOf(true, false, false, true)

    @Suppress("unused", "UNUSED_PARAMETER")
    internal class SetFake {

        lateinit var integer: Set<Int>

        fun boolean(value: Set<Boolean>) {}

        fun noType(@Converter(SetConverter::class) value: Set<SetFake>) {}
    }
}
