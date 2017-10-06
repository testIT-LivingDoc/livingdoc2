package org.livingdoc.converters.collection

import org.livingdoc.api.conversion.Converter

internal class SetConverterTest : CollectionWithSingleParamContract<Set<Any>>() {

    override val cut = SetConverter()
    override val collectionClass = Set::class.java
    override val fixtureClass = SetFake::class
    override val intExpectation = setOf(1, 2, 3, 4)
    override val booleanExpectation = setOf(true, false, false, true)

    @Suppress("unused", "UNUSED_PARAMETER")
    internal class SetFake {

        @Converter(SetConverter::class)
        lateinit var integer: Set<Int>

        fun boolean(@Converter(SetConverter::class) value: Set<Boolean>) {}

        fun noType(@Converter(SetConverter::class) value: Set<SetFake>) {}
    }
}



