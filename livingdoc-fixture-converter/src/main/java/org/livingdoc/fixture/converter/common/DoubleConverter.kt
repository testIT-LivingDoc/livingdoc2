package org.livingdoc.fixture.converter.common


open class DoubleConverter : AbstractNumberConverter<Double>() {
    override fun convertToTarget(number: Number): Double = number.toDouble()
}
