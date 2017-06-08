package org.livingdoc.fixture.converter.common

open class FloatConverter : AbstractNumberConverter<Float>() {
    override fun convertToTarget(number: Number): Float = number.toFloat()
}
