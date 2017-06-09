package org.livingdoc.fixture.converter.common


open class ShortConverter : AbstractNumberConverter<Short>() {
    override fun convertToTarget(number: Number): Short = number.toShort()
}
