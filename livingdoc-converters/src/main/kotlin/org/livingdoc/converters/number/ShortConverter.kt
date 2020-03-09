package org.livingdoc.converters.number

import java.math.BigDecimal
import kotlin.reflect.KClass

/**
 * This converter converts a BigDecimal number to a Short
 */
open class ShortConverter : AbstractNumberConverter<Short>() {

    override val lowerBound: Short = Short.MIN_VALUE
    override val upperBound: Short = Short.MAX_VALUE

    /**
     * This function returns the value of number as a Short
     */
    override fun convertToTarget(number: BigDecimal): Short = number.toShort()

    override fun canConvertTo(targetType: KClass<*>): Boolean {
        return Short::class == targetType
    }
}
