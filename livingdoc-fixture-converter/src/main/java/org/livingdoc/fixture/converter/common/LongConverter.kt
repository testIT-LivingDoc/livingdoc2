package org.livingdoc.fixture.converter.common


open class LongConverter : AbstractNumberConverter<Long>() {
    override fun convertToTarget(number: Number): Long = number.toLong()
}
