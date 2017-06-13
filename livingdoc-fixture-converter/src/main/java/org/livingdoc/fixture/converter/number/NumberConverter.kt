package org.livingdoc.fixture.converter.number

open class NumberConverter : AbstractNumberConverter<Number>() {
    override fun convertToTarget(number: Number): Number = number
}
