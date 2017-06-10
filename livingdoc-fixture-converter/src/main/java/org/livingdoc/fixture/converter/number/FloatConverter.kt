package org.livingdoc.fixture.converter.number

open class FloatConverter : AbstractNumberConverter<Float>() {
    override fun convertToTarget(number: Number): Float = number.toFloat()
}
