package org.livingdoc.fixture.converter.number


open class IntegerConverter : AbstractNumberConverter<Int>() {
    override fun convertToTarget(number: Number): Int = number.toInt()
}
