package org.livingdoc.fixture.converter.common

open class NumberConverter : AbstractNumberConverter<Number>() {
    override fun convertToTarget(number: Number): Number = number
}
