package org.livingdoc.fixture.converter.number


open class ByteConverter : AbstractNumberConverter<Byte>() {
    override fun convertToTarget(number: Number): Byte = number.toByte()
}
