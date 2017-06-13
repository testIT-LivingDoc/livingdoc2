package org.livingdoc.fixture.converter.number


open class LongConverter : AbstractNumberConverter<Long>() {
    override fun convertToTarget(number: Number): Long = number.toLong()
}
