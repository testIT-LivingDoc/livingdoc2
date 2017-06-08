package org.livingdoc.fixture.converter.common


open class IntegerConverter : AbstractNumberConverter<Int>() {
    override fun convertToTarget(number: Number): Int = number.toInt()
}
