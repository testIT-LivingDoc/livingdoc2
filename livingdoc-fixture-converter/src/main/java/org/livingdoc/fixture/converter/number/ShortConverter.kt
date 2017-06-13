package org.livingdoc.fixture.converter.number


open class ShortConverter : AbstractNumberConverter<Short>() {
    override fun convertToTarget(number: Number): Short = number.toShort()
}
