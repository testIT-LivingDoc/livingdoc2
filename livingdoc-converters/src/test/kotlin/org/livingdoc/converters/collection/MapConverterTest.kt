package org.livingdoc.converters.collection

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.livingdoc.api.conversion.ConversionException
import org.livingdoc.api.conversion.Converter

internal class MapConverterTest : CollectionConverterContract() {

    override val cut = MapConverter()
    override val collectionClass = Map::class.java
    override val fixtureClass = MapFake::class
    override val booleanInput: String = "true, true; false, false"
    override val intInput: String = "1, 1; 2, 2; 3, 3"
    override val intExpectation = hashMapOf("1" to 1, "2" to 2, "3" to 3)
    override val booleanExpectation = hashMapOf("true" to true, "false" to false)

    @Test
    fun `throws error on not matching key value count`() {
        val input = "1, 1; 2, 2; 3 "
        val annotatedElement = fakeIntegerField()

        Assertions.assertThrows(ConversionException::class.java) {
            cut.convert(input, annotatedElement, null)
        }
    }


    @Suppress("unused", "UNUSED_PARAMETER")
    internal class MapFake {

        @Converter(MapConverter::class)
        lateinit var integer: Map<String, Int>

        fun boolean(@Converter(MapConverter::class) value: Map<String, Boolean>) {}

        fun noType(@Converter(MapConverter::class) value: Map<MapFake, MapFake>) {}
    }
}
