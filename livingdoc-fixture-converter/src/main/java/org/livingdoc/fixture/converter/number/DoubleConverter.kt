package org.livingdoc.fixture.converter.number


open class DoubleConverter : AbstractNumberConverter<Double>() {
    override fun convertToTarget(number: Number): Double = number.toDouble()
}
