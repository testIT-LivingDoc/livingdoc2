package org.livingdoc.fixture.converter.common


open class ByteConverter : AbstractNumberConverter<Byte>() {
    override fun convertToTarget(number: Number): Byte = number.toByte()
}
